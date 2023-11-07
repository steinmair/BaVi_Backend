package at.htlklu.bavi.configs;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FaviconConfiguration
{
    // not working
//    @Bean
//    public SimpleUrlHandlerMapping customFaviconHandlerMapping()
//    {
//        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
//        mapping.setOrder(Integer.MIN_VALUE);
//        mapping.setUrlMap(Collections.singletonMap("/static/images/Umbrella.png", faviconRequestHandler()));
//        return mapping;
//    }
//
//    @Bean
//    protected ResourceHttpRequestHandler faviconRequestHandler()
//    {
//        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
//        requestHandler.setLocations(Collections.singletonList(new ClassPathResource("/")));
//        return requestHandler;
//    }


//    @Bean
//    public SimpleUrlHandlerMapping myFaviconHandlerMapping()
//    {
//        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
//        mapping.setOrder(Integer.MIN_VALUE);
//        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", myFaviconRequestHandler()));
//        return mapping;
//    }
//
//    @Autowired
//    ApplicationContext applicationContext;
//
//    @Bean
//    protected ResourceHttpRequestHandler myFaviconRequestHandler()
//    {
//        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
//        requestHandler.setLocations(Arrays.<Resource> asList(applicationContext.getResource("/")));
//        requestHandler.setCacheSeconds(0);
//        return requestHandler;
//    }

}
