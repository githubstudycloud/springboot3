package com.platform.auth.infrastructure.security;

import com.platform.auth.domain.model.Permission;
import com.platform.auth.domain.model.Role;
import com.platform.auth.domain.model.User;
import com.platform.auth.domain.service.UserDomainService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT用户详情服务
 * <p>
 * 实现Spring Security的UserDetailsService接口，为认证系统提供用户信息
 * </p>
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    
    private final UserDomainService userDomainService;
    
    public JwtUserDetailsService(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDomainService.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }
    
    /**
     * 获取用户权限列表
     *
     * @param user 用户
     * @return 权限列表
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // 收集角色权限
        List<String> authorities = new ArrayList<>();
        
        // 添加角色权限（ROLE_前缀）
        for (Role role : user.getRoles()) {
            if (role.isEnabled()) {
                authorities.add("ROLE_" + role.getCode());
                
                // 添加角色下的所有权限
                for (Permission permission : role.getPermissions()) {
                    if (permission.isEnabled()) {
                        authorities.add(permission.getCode());
                    }
                }
            }
        }
        
        // 去重
        return authorities.stream()
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
