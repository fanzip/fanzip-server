package org.example.fanzip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:/application.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@MapperScan(basePackages = {
        "org.example.fanzip.fancard.mapper",
        "org.example.fanzip.user.mapper",
        "org.example.fanzip.payment.mapper",
        "org.example.fanzip.market.mapper",
        "org.example.fanzip.cart.mapper",
        "org.example.fanzip.membership.mapper",
        "org.example.fanzip.influencer.mapper"
})
@ComponentScan(basePackages = {
        "org.example.fanzip",
        "org.example.fanzip.user.service",
        "org.example.fanzip.payment",
        "org.example.fanzip.market",
        "org.example.fanzip.cart",
        "org.example.fanzip.membership.service",
        "org.example.fanzip.influencer.service"
}, excludeFilters = @ComponentScan.Filter(org.springframework.stereotype.Controller.class))
public class RootConfig {
    @Value("${spring.datasource.driver-class-name}") String driver;
    @Value("${spring.datasource.url}") String url;
    @Value("${spring.datasource.username}") String username;
    @Value("${spring.datasource.password}") String password;


    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setConfigLocation(
                applicationContext.getResource("classpath:/mybatis-config.xml"));
        sqlSessionFactory.setMapperLocations(
                applicationContext.getResources("classpath:/mappers/*.xml"));
        sqlSessionFactory.setDataSource(dataSource());
        return (SqlSessionFactory) sqlSessionFactory.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
        return manager;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}