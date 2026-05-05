package com.foodexpress.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.cloudinary.Cloudinary;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

/*    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/food_express_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true");
        ds.setUsername("root");
        ds.setPassword("root");

        return ds;
    }
*/
	
	
	@Bean
	public DataSource dataSource() {
	    DriverManagerDataSource ds = new DriverManagerDataSource();
	    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");

	    // The CORRECTED URL with the ".a" and the required SSL settings
	    ds.setUrl("jdbc:mysql://mysql-346e1dff-meharbansindal-6e10.a.aivencloud.com:10682/defaultdb" +
	              "?useSSL=true" +
	              "&trustServerCertificate=true" +
	              "&sslMode=REQUIRED");

	    ds.setUsername("avnadmin");
	    ds.setPassword("AVNS_HjyHxy4bB1GSdbhvDwz"); // Use your real password here

	    return ds;
	}
	
	
	@Bean
	public Cloudinary cloudinary() {
	    Map<String,String> config = new HashMap<String, String>();
	    config.put("cloud_name", "ddv82qdmk");
	    config.put("api_key", "993224374331781");
	    config.put("api_secret", "Gi86aUwS7sin-cG0U_3qWhk5QlM");
	    return new Cloudinary(config);
	}
	
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();

        factory.setDataSource(dataSource());
        factory.setPackagesToScan("com.foodexpress.model");
        factory.setHibernateProperties(hibernateProperties());

        return factory;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();

        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "update");

        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}