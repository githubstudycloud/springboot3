# ==================================================
# SpringBoot + Vue 项目构建脚本 (PowerShell)
# ==================================================

param(
    [string]$Action = "build",
    [string]$Environment = "dev",
    [switch]$SkipTests = $false,
    [switch]$Clean = $false,
    [switch]$Help = $false
)

# 设置脚本编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# 颜色定义
$ColorRed = "Red"
$ColorGreen = "Green"
$ColorYellow = "Yellow"
$ColorBlue = "Blue"
$ColorCyan = "Cyan"

# 打印函数
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $ColorBlue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $ColorGreen
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $ColorYellow
}

function Write-Error-Custom {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $ColorRed
}

function Write-Title {
    param([string]$Title)
    Write-Host ""
    Write-Host "==================== $Title ====================" -ForegroundColor $ColorCyan
    Write-Host ""
}

# 显示帮助信息
function Show-Help {
    Write-Host @"
SpringBoot + Vue 项目构建脚本

用法:
    .\scripts\build.ps1 [选项]

选项:
    -Action <action>      操作类型: build, start, stop, clean, test (默认: build)
    -Environment <env>    环境: dev, test, prod (默认: dev)  
    -SkipTests           跳过测试
    -Clean               清理构建产物
    -Help                显示此帮助信息

示例:
    .\scripts\build.ps1                           # 默认构建
    .\scripts\build.ps1 -Action start             # 启动服务
    .\scripts\build.ps1 -Action build -Clean      # 清理并构建
    .\scripts\build.ps1 -Action test              # 运行测试
    .\scripts\build.ps1 -Environment prod         # 生产环境构建

操作说明:
    build    - 构建前端和后端项目
    start    - 启动开发环境
    stop     - 停止所有服务
    clean    - 清理构建产物
    test     - 运行测试
    init     - 初始化项目环境
"@
}

# 检查必要的工具
function Test-Prerequisites {
    Write-Info "检查必要的开发工具..."
    
    $tools = @(
        @{Name="Java"; Command="java"; VersionCmd="java -version"}
        @{Name="Maven"; Command="mvn"; VersionCmd="mvn -version"}
        @{Name="Node.js"; Command="node"; VersionCmd="node --version"}
        @{Name="npm"; Command="npm"; VersionCmd="npm --version"}
    )
    
    $allFound = $true
    
    foreach ($tool in $tools) {
        try {
            $null = Get-Command $tool.Command -ErrorAction Stop
            $version = Invoke-Expression $tool.VersionCmd 2>$null | Select-Object -First 1
            Write-Success "$($tool.Name) 已安装: $version"
        }
        catch {
            Write-Error-Custom "$($tool.Name) 未安装或不在 PATH 中"
            $allFound = $false
        }
    }
    
    if (-not $allFound) {
        Write-Error-Custom "请安装缺失的工具后重试"
        exit 1
    }
}

# 清理构建产物
function Invoke-Clean {
    Write-Title "清理构建产物"
    
    # 清理后端
    if (Test-Path "backend") {
        Write-Info "清理后端构建产物..."
        Push-Location "backend"
        try {
            & mvn clean | Out-Host
            if ($LASTEXITCODE -eq 0) {
                Write-Success "后端清理完成"
            } else {
                Write-Error-Custom "后端清理失败"
            }
        }
        finally {
            Pop-Location
        }
    }
    
    # 清理前端
    if (Test-Path "frontend") {
        Write-Info "清理前端构建产物..."
        Push-Location "frontend"
        try {
            if (Test-Path "dist") {
                Remove-Item "dist" -Recurse -Force
                Write-Success "删除前端 dist 目录"
            }
            if (Test-Path "node_modules") {
                Write-Info "删除 node_modules 目录..."
                Remove-Item "node_modules" -Recurse -Force
                Write-Success "删除前端 node_modules 目录"
            }
        }
        finally {
            Pop-Location
        }
    }
    
    # 清理临时文件
    Write-Info "清理临时文件..."
    $tempFiles = @("*.log", "*.pid", "temp", "tmp", ".cache")
    foreach ($pattern in $tempFiles) {
        Get-ChildItem -Path "." -Name $pattern -Recurse | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
    }
    
    Write-Success "清理完成"
}

# 构建后端
function Build-Backend {
    Write-Title "构建后端项目"
    
    if (-not (Test-Path "backend")) {
        Write-Error-Custom "backend 目录不存在"
        return $false
    }
    
    Push-Location "backend"
    try {
        Write-Info "开始构建 Spring Boot 项目..."
        
        $mvnArgs = @("clean", "package")
        if ($SkipTests) {
            $mvnArgs += "-DskipTests"
        }
        if ($Environment -eq "prod") {
            $mvnArgs += "-Pprod"
        }
        
        & mvn $mvnArgs | Out-Host
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "后端构建成功"
            return $true
        } else {
            Write-Error-Custom "后端构建失败"
            return $false
        }
    }
    finally {
        Pop-Location
    }
}

# 构建前端
function Build-Frontend {
    Write-Title "构建前端项目"
    
    if (-not (Test-Path "frontend")) {
        Write-Error-Custom "frontend 目录不存在"
        return $false
    }
    
    Push-Location "frontend"
    try {
        # 安装依赖
        if (-not (Test-Path "node_modules")) {
            Write-Info "安装前端依赖..."
            & npm install | Out-Host
            if ($LASTEXITCODE -ne 0) {
                Write-Error-Custom "前端依赖安装失败"
                return $false
            }
        }
        
        # 构建
        Write-Info "开始构建 Vue 项目..."
        if ($Environment -eq "prod") {
            & npm run build | Out-Host
        } else {
            & npm run build:$Environment | Out-Host
        }
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "前端构建成功"
            return $true
        } else {
            Write-Error-Custom "前端构建失败"
            return $false
        }
    }
    finally {
        Pop-Location
    }
}

# 运行测试
function Invoke-Tests {
    Write-Title "运行项目测试"
    
    $allPassed = $true
    
    # 后端测试
    if (Test-Path "backend") {
        Write-Info "运行后端测试..."
        Push-Location "backend"
        try {
            & mvn test | Out-Host
            if ($LASTEXITCODE -eq 0) {
                Write-Success "后端测试通过"
            } else {
                Write-Error-Custom "后端测试失败"
                $allPassed = $false
            }
        }
        finally {
            Pop-Location
        }
    }
    
    # 前端测试
    if (Test-Path "frontend") {
        Write-Info "运行前端测试..."
        Push-Location "frontend"
        try {
            if (Test-Path "package.json") {
                $packageJson = Get-Content "package.json" | ConvertFrom-Json
                if ($packageJson.scripts.test) {
                    & npm test | Out-Host
                    if ($LASTEXITCODE -eq 0) {
                        Write-Success "前端测试通过"
                    } else {
                        Write-Error-Custom "前端测试失败"
                        $allPassed = $false
                    }
                } else {
                    Write-Warning "前端项目没有定义测试脚本"
                }
            }
        }
        finally {
            Pop-Location
        }
    }
    
    if ($allPassed) {
        Write-Success "所有测试通过"
    } else {
        Write-Error-Custom "部分测试失败"
    }
    
    return $allPassed
}

# 启动开发环境
function Start-Development {
    Write-Title "启动开发环境"
    
    # 启动数据库
    Write-Info "启动数据库服务..."
    try {
        $dockerCheck = Get-Command docker -ErrorAction SilentlyContinue
        if ($dockerCheck) {
            & docker-compose up -d mysql redis | Out-Host
            Write-Success "数据库服务启动完成"
            Start-Sleep -Seconds 10
        } else {
            Write-Warning "Docker 未安装，跳过数据库启动"
        }
    }
    catch {
        Write-Warning "数据库启动失败，请手动启动"
    }
    
    # 启动后端
    if (Test-Path "backend") {
        Write-Info "启动后端服务..."
        Start-Process -FilePath "cmd" -ArgumentList "/c", "cd backend && mvn spring-boot:run" -WindowStyle Normal
        Write-Success "后端服务启动中..."
    }
    
    # 启动前端
    if (Test-Path "frontend") {
        Write-Info "启动前端服务..."
        Start-Process -FilePath "cmd" -ArgumentList "/c", "cd frontend && npm run dev" -WindowStyle Normal
        Write-Success "前端服务启动中..."
    }
    
    Write-Info "服务启动完成，请等待服务完全启动"
    Write-Info "前端地址: http://localhost:5173"
    Write-Info "后端地址: http://localhost:8080"
}

# 停止服务
function Stop-Services {
    Write-Title "停止所有服务"
    
    # 停止 Java 进程
    Write-Info "停止 Java 进程..."
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
    
    # 停止 Node 进程
    Write-Info "停止 Node.js 进程..."
    Get-Process -Name "node" -ErrorAction SilentlyContinue | Stop-Process -Force
    
    # 停止 Docker 容器
    try {
        $dockerCheck = Get-Command docker-compose -ErrorAction SilentlyContinue
        if ($dockerCheck) {
            Write-Info "停止 Docker 容器..."
            & docker-compose down | Out-Host
        }
    }
    catch {
        Write-Warning "Docker Compose 停止失败"
    }
    
    Write-Success "所有服务已停止"
}

# 初始化项目环境
function Initialize-Project {
    Write-Title "初始化项目环境"
    
    # 检查工具
    Test-Prerequisites
    
    # 创建必要的目录
    $directories = @("logs", "temp", "uploads", "downloads")
    foreach ($dir in $directories) {
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
            Write-Success "创建目录: $dir"
        }
    }
    
    # 初始化前端项目
    if (Test-Path "frontend/package.json") {
        Write-Info "初始化前端项目..."
        Push-Location "frontend"
        try {
            & npm install | Out-Host
            Write-Success "前端依赖安装完成"
        }
        finally {
            Pop-Location
        }
    }
    
    Write-Success "项目环境初始化完成"
}

# 主函数
function Main {
    if ($Help) {
        Show-Help
        return
    }
    
    Write-Title "SpringBoot + Vue 项目构建工具"
    Write-Info "操作: $Action"
    Write-Info "环境: $Environment"
    
    switch ($Action.ToLower()) {
        "build" {
            if ($Clean) {
                Invoke-Clean
            }
            Test-Prerequisites
            $backendResult = Build-Backend
            $frontendResult = Build-Frontend
            
            if ($backendResult -and $frontendResult) {
                Write-Success "项目构建完成"
            } else {
                Write-Error-Custom "项目构建失败"
                exit 1
            }
        }
        "start" {
            Start-Development
        }
        "stop" {
            Stop-Services
        }
        "clean" {
            Invoke-Clean
        }
        "test" {
            $testResult = Invoke-Tests
            if (-not $testResult) {
                exit 1
            }
        }
        "init" {
            Initialize-Project
        }
        default {
            Write-Error-Custom "未知操作: $Action"
            Show-Help
            exit 1
        }
    }
}

# 执行主函数
Main 