package edu.mit;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MVCConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
            super.configurePathMatch(configurer);
            configurer.setUseRegisteredSuffixPatternMatch(false);
            configurer.setUseSuffixPatternMatch(false);
        }

}
