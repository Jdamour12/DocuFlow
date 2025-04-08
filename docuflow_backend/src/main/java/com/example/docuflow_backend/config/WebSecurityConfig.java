// package com.example.docuflow_backend.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
// import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// import java.util.Arrays;

// @Configuration
// public class WebSecurityConfig {

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .cors(Customizer.withDefaults())
//             .csrf(csrf -> csrf
//                 .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                 .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
//             )
//             .authorizeHttpRequests(authorize -> authorize
//                 .requestMatchers("/api/auth/**").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .formLogin(form -> form
//                 .loginProcessingUrl("/api/auth/login")
//                 .successHandler((request, response, authentication) -> {
//                     response.setStatus(200);
//                     response.getWriter().write("{\"success\":true,\"username\":\"" + authentication.getName() + "\"}");
//                 })
//                 .failureHandler((request, response, exception) -> {
//                     response.setStatus(401);
//                     response.getWriter().write("{\"success\":false,\"message\":\"" + exception.getMessage() + "\"}");
//                 })
//             )
//             .logout(logout -> logout
//                 .logoutUrl("/api/auth/logout")
//                 .logoutSuccessHandler((request, response, authentication) -> {
//                     response.setStatus(200);
//                     response.getWriter().write("{\"success\":true}");
//                 })
//             );

//         return http.build();
//     }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(Arrays.asList("*"));
//         configuration.setAllowCredentials(true);
        
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }

//     @Autowired
//     public void configure(AuthenticationManagerBuilder auth) throws Exception {
//         auth
//             .ldapAuthentication()
//                 .userDnPatterns("uid={0},ou=DocuFlowUsers")
//                 .groupSearchBase("ou=DocuFlowUsers")
//                 .contextSource()
//                 .url("ldap://localhost:8389/dc=example,dc=com")
//                 .and()
//             .passwordCompare()
//                 .passwordEncoder(new BCryptPasswordEncoder())
//                 .passwordAttribute("userPassword");
//     }
// }
