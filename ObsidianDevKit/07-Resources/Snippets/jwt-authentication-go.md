---
title: JWT认证完整实现 (Go)
language: go
category: authentication
tags: [snippet, go, jwt, security, authentication]
created: 2024-12-10
tested: yes
---

# 🔖 JWT认证完整实现 (Go)

## 📝 描述
> 这段代码做什么用的

Go语言中使用JWT（JSON Web Token）实现用户认证的完整方案，包括token生成、验证、刷新和中间件实现。

## 🏷️ 标签
`go` `jwt` `authentication` `middleware` `security`

## 💻 代码

```go
// JWT认证完整实现
// 作者：DevTeam
// 创建时间：2024-12-10
// 用途：用户认证系统

package auth

import (
    "errors"
    "time"

    "github.com/gin-gonic/gin"
    "github.com/golang-jwt/jwt/v5"
)

// 配置
var (
    jwtSecret = []byte("your-secret-key") // 生产环境应从环境变量读取
    jwtIssuer = "your-app-name"
)

// Claims 自定义JWT声明
type Claims struct {
    UserID   string `json:"user_id"`
    Username string `json:"username"`
    Role     string `json:"role"`
    jwt.RegisteredClaims
}

// TokenPair token对
type TokenPair struct {
    AccessToken  string `json:"access_token"`
    RefreshToken string `json:"refresh_token"`
}

// GenerateTokenPair 生成访问令牌和刷新令牌
func GenerateTokenPair(userID, username, role string) (*TokenPair, error) {
    now := time.Now()
    
    // 访问令牌 - 15分钟有效
    accessClaims := Claims{
        UserID:   userID,
        Username: username,
        Role:     role,
        RegisteredClaims: jwt.RegisteredClaims{
            Issuer:    jwtIssuer,
            Subject:   userID,
            ExpiresAt: jwt.NewNumericDate(now.Add(15 * time.Minute)),
            NotBefore: jwt.NewNumericDate(now),
            IssuedAt:  jwt.NewNumericDate(now),
        },
    }
    
    accessToken := jwt.NewWithClaims(jwt.SigningMethodHS256, accessClaims)
    accessTokenString, err := accessToken.SignedString(jwtSecret)
    if err != nil {
        return nil, err
    }
    
    // 刷新令牌 - 7天有效
    refreshClaims := Claims{
        UserID: userID,
        RegisteredClaims: jwt.RegisteredClaims{
            Issuer:    jwtIssuer,
            Subject:   userID,
            ExpiresAt: jwt.NewNumericDate(now.Add(7 * 24 * time.Hour)),
            NotBefore: jwt.NewNumericDate(now),
            IssuedAt:  jwt.NewNumericDate(now),
        },
    }
    
    refreshToken := jwt.NewWithClaims(jwt.SigningMethodHS256, refreshClaims)
    refreshTokenString, err := refreshToken.SignedString(jwtSecret)
    if err != nil {
        return nil, err
    }
    
    return &TokenPair{
        AccessToken:  accessTokenString,
        RefreshToken: refreshTokenString,
    }, nil
}

// ValidateToken 验证token
func ValidateToken(tokenString string) (*Claims, error) {
    token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
        // 验证签名方法
        if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
            return nil, errors.New("unexpected signing method")
        }
        return jwtSecret, nil
    })
    
    if err != nil {
        return nil, err
    }
    
    if claims, ok := token.Claims.(*Claims); ok && token.Valid {
        return claims, nil
    }
    
    return nil, errors.New("invalid token")
}

// RefreshAccessToken 使用刷新令牌生成新的访问令牌
func RefreshAccessToken(refreshTokenString string) (*TokenPair, error) {
    claims, err := ValidateToken(refreshTokenString)
    if err != nil {
        return nil, err
    }
    
    // 从数据库获取最新的用户信息
    // user, err := getUserByID(claims.UserID)
    // if err != nil {
    //     return nil, err
    // }
    
    // 这里简化处理，实际应从数据库获取
    return GenerateTokenPair(claims.UserID, claims.Username, claims.Role)
}

// AuthMiddleware Gin中间件
func AuthMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        // 从Header获取token
        authHeader := c.GetHeader("Authorization")
        if authHeader == "" {
            c.JSON(401, gin.H{"error": "authorization header required"})
            c.Abort()
            return
        }
        
        // Bearer token
        tokenString := authHeader
        if len(authHeader) > 7 && authHeader[:7] == "Bearer " {
            tokenString = authHeader[7:]
        }
        
        // 验证token
        claims, err := ValidateToken(tokenString)
        if err != nil {
            c.JSON(401, gin.H{"error": "invalid token"})
            c.Abort()
            return
        }
        
        // 将用户信息存入上下文
        c.Set("user_id", claims.UserID)
        c.Set("username", claims.Username)
        c.Set("role", claims.Role)
        
        c.Next()
    }
}

// RoleMiddleware 角色权限中间件
func RoleMiddleware(allowedRoles ...string) gin.HandlerFunc {
    return func(c *gin.Context) {
        role, exists := c.Get("role")
        if !exists {
            c.JSON(403, gin.H{"error": "role not found"})
            c.Abort()
            return
        }
        
        userRole := role.(string)
        allowed := false
        for _, r := range allowedRoles {
            if userRole == r {
                allowed = true
                break
            }
        }
        
        if !allowed {
            c.JSON(403, gin.H{"error": "insufficient permissions"})
            c.Abort()
            return
        }
        
        c.Next()
    }
}

// 使用示例
func Example() {
    r := gin.Default()
    
    // 登录端点
    r.POST("/login", func(c *gin.Context) {
        // 验证用户凭据...
        // 生成token
        tokens, err := GenerateTokenPair("123", "john_doe", "admin")
        if err != nil {
            c.JSON(500, gin.H{"error": "failed to generate token"})
            return
        }
        
        c.JSON(200, tokens)
    })
    
    // 刷新token端点
    r.POST("/refresh", func(c *gin.Context) {
        var req struct {
            RefreshToken string `json:"refresh_token"`
        }
        
        if err := c.ShouldBindJSON(&req); err != nil {
            c.JSON(400, gin.H{"error": "invalid request"})
            return
        }
        
        tokens, err := RefreshAccessToken(req.RefreshToken)
        if err != nil {
            c.JSON(401, gin.H{"error": "invalid refresh token"})
            return
        }
        
        c.JSON(200, tokens)
    })
    
    // 受保护的路由
    protected := r.Group("/api")
    protected.Use(AuthMiddleware())
    {
        // 所有用户可访问
        protected.GET("/profile", func(c *gin.Context) {
            userID := c.GetString("user_id")
            c.JSON(200, gin.H{"user_id": userID})
        })
        
        // 仅管理员可访问
        protected.GET("/admin", RoleMiddleware("admin"), func(c *gin.Context) {
            c.JSON(200, gin.H{"message": "admin area"})
        })
    }
    
    r.Run(":8080")
}
```

## 📋 使用方法

### 基本用法
```go
// 1. 生成token
tokens, err := GenerateTokenPair("user123", "john", "user")

// 2. 验证token
claims, err := ValidateToken(tokenString)

// 3. 在Gin中使用中间件
router.Use(AuthMiddleware())
```

### 参数说明
| 参数 | 类型 | 必需 | 默认值 | 说明 |
|------|------|------|--------|------|
| userID | string | 是 | - | 用户唯一标识 |
| username | string | 是 | - | 用户名 |
| role | string | 是 | - | 用户角色 |

### 返回值
- **类型**: `*TokenPair`
- **说明**: 包含访问令牌和刷新令牌

## 🧪 测试用例

### 测试1：正常生成和验证
```go
func TestGenerateAndValidate(t *testing.T) {
    tokens, err := GenerateTokenPair("123", "test", "user")
    assert.NoError(t, err)
    assert.NotEmpty(t, tokens.AccessToken)
    
    claims, err := ValidateToken(tokens.AccessToken)
    assert.NoError(t, err)
    assert.Equal(t, "123", claims.UserID)
}
```

### 测试2：过期token
```go
func TestExpiredToken(t *testing.T) {
    // 创建一个已过期的token
    claims := Claims{
        UserID: "123",
        RegisteredClaims: jwt.RegisteredClaims{
            ExpiresAt: jwt.NewNumericDate(time.Now().Add(-1 * time.Hour)),
        },
    }
    
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    tokenString, _ := token.SignedString(jwtSecret)
    
    _, err := ValidateToken(tokenString)
    assert.Error(t, err)
}
```

## ⚡ 性能
- **生成token**: ~0.5ms
- **验证token**: ~0.1ms
- **内存占用**: 极低

## 🔧 配置/依赖
```bash
# 安装依赖
go get github.com/golang-jwt/jwt/v5
go get github.com/gin-gonic/gin
```

## 📌 注意事项
- ⚠️ JWT密钥必须安全存储，建议使用环境变量
- ⚠️ Access Token时间不宜过长，建议15-30分钟
- ⚠️ Refresh Token应该存储在数据库中，支持撤销
- ⚠️ 生产环境应使用HTTPS传输

## 🔄 版本历史
| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2024-12-10 | 初始版本 | DevTeam |
| v1.1 | 2024-12-15 | 添加角色中间件 | DevTeam |

## 🔗 相关代码
- [[password-hashing-bcrypt]]
- [[session-management]]
- [[oauth2-implementation]]

## 📚 参考资料
- [JWT官网](https://jwt.io/)
- [Go JWT库文档](https://github.com/golang-jwt/jwt)
- [[authentication-best-practices]]

---

### 快速复制

<details>
<summary>点击展开完整代码</summary>

```go
package auth

import (
    "errors"
    "time"
    "github.com/gin-gonic/gin"
    "github.com/golang-jwt/jwt/v5"
)

var (
    jwtSecret = []byte("your-secret-key")
    jwtIssuer = "your-app-name"
)

type Claims struct {
    UserID   string `json:"user_id"`
    Username string `json:"username"`
    Role     string `json:"role"`
    jwt.RegisteredClaims
}

type TokenPair struct {
    AccessToken  string `json:"access_token"`
    RefreshToken string `json:"refresh_token"`
}

func GenerateTokenPair(userID, username, role string) (*TokenPair, error) {
    now := time.Now()
    
    accessClaims := Claims{
        UserID:   userID,
        Username: username,
        Role:     role,
        RegisteredClaims: jwt.RegisteredClaims{
            Issuer:    jwtIssuer,
            Subject:   userID,
            ExpiresAt: jwt.NewNumericDate(now.Add(15 * time.Minute)),
            NotBefore: jwt.NewNumericDate(now),
            IssuedAt:  jwt.NewNumericDate(now),
        },
    }
    
    accessToken := jwt.NewWithClaims(jwt.SigningMethodHS256, accessClaims)
    accessTokenString, err := accessToken.SignedString(jwtSecret)
    if err != nil {
        return nil, err
    }
    
    refreshClaims := Claims{
        UserID: userID,
        RegisteredClaims: jwt.RegisteredClaims{
            Issuer:    jwtIssuer,
            Subject:   userID,
            ExpiresAt: jwt.NewNumericDate(now.Add(7 * 24 * time.Hour)),
            NotBefore: jwt.NewNumericDate(now),
            IssuedAt:  jwt.NewNumericDate(now),
        },
    }
    
    refreshToken := jwt.NewWithClaims(jwt.SigningMethodHS256, refreshClaims)
    refreshTokenString, err := refreshToken.SignedString(jwtSecret)
    if err != nil {
        return nil, err
    }
    
    return &TokenPair{
        AccessToken:  accessTokenString,
        RefreshToken: refreshTokenString,
    }, nil
}

func ValidateToken(tokenString string) (*Claims, error) {
    token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
        if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
            return nil, errors.New("unexpected signing method")
        }
        return jwtSecret, nil
    })
    
    if err != nil {
        return nil, err
    }
    
    if claims, ok := token.Claims.(*Claims); ok && token.Valid {
        return claims, nil
    }
    
    return nil, errors.New("invalid token")
}

func AuthMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        authHeader := c.GetHeader("Authorization")
        if authHeader == "" {
            c.JSON(401, gin.H{"error": "authorization header required"})
            c.Abort()
            return
        }
        
        tokenString := authHeader
        if len(authHeader) > 7 && authHeader[:7] == "Bearer " {
            tokenString = authHeader[7:]
        }
        
        claims, err := ValidateToken(tokenString)
        if err != nil {
            c.JSON(401, gin.H{"error": "invalid token"})
            c.Abort()
            return
        }
        
        c.Set("user_id", claims.UserID)
        c.Set("username", claims.Username)
        c.Set("role", claims.Role)
        
        c.Next()
    }
}
```

</details>

---
*最后更新：2024-12-17*