/*
 *
 */
package sk.antons.servlet.filter.adhoc;

import sk.antons.servlet.filter.LogFilter;
import sk.antons.servlet.filter.builder.LogFilterBuilder;

/**
 *
 * @author antons
 */
public class CompileTestik {

    public static void main(String[] argv) {

        LogFilter filter = LogFilterBuilder.instance()
            .defaultConf()
                .messageConsumer(m -> System.out.println(m))
                .messageConsumerEnabled(() -> true)
                .identity(true)
                .requestHeaderFormatter(LogFilter.Headers.listed("Content-Type"))
                .requestPayloadFormatter(LogFilter.Body.asIs())
                .done()
            .inCase()
                .request()
                    .lb()
                       .path().startsWith("/pokus")
                       .and()
                       .method().equals("GET")
                       .and()
                       .schema().equals("http")
                    .rb()
                    .done()
                .response()
                    .not()
                    .statusOK()
                    .done()
                .done()
            .inCase()
                .request()
                    .lb()
                       .path().startsWith("/pokus2")
                       .and()
                       .method().equals("GET")
                       .and()
                       .schema().equals("http")
                    .rb()
                    .done()
                .conf()
                    .requestHeaderFormatter(LogFilter.Headers.all())
                    .requestPayloadFormatter(LogFilter.Body.asIs())
                    .responsePrefix(null)
                    .done()
                .done()
            .build();

        System.out.println(" conf: " + filter.configurationInfo());

    }
}
