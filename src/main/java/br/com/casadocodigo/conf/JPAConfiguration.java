package br.com.casadocodigo.conf;

import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
public class JPAConfiguration {
		
	 	@Bean
	    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
	        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

	        factoryBean.setJpaVendorAdapter(vendorAdapter);

	        DriverManagerDataSource dataSource = new DriverManagerDataSource();
	        String dbHost = getEnvOrDefault("DB_HOST", "mysql");
	        String dbPort = getEnvOrDefault("DB_PORT", "3306");
	        String dbName = getEnvOrDefault("DB_NAME", "casadocodigo");
	        String dbUser = getEnvOrDefault("DB_USER", "casadocodigo");
	        String dbPassword = getEnvOrDefault("DB_PASSWORD", "casadocodigo");
	        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", dbHost, dbPort, dbName);
	        dataSource.setUsername(dbUser);
	        dataSource.setPassword(dbPassword);
	        dataSource.setUrl(jdbcUrl);
	        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

	        factoryBean.setDataSource(dataSource);

	        Properties props = new Properties();
	        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
	        props.setProperty("hibernate.show_sql", "true");
	        props.setProperty("hibernate.hbm2ddl.auto", "update");

	        factoryBean.setJpaProperties(props);

	        factoryBean.setPackagesToScan("br.com.casadocodigo.loja.models");

	        return factoryBean;
	    }
	 
	 private String getEnvOrDefault(String key, String defaultValue) {
	     String value = System.getenv(key);
	     return (value != null && !value.isBlank()) ? value : defaultValue;
	 }
}
