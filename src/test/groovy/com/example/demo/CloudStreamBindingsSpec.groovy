package com.example.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.TestExecutionListeners
import spock.lang.Specification

import java.util.function.Function

@SpringBootTest(args = "--spring.cloud.stream.function.definition=uppercase")
@TestExecutionListeners(
        listeners = MyTestExecutionListener,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
class CloudStreamBindingsSpec extends Specification {

    @Autowired
    private InputDestination source

    @Autowired
    private OutputDestination target

    def sampleTest() {
        when:
        source.send(new GenericMessage<byte[]>("hello".getBytes()))

        then:
        target.receive().getPayload() == "HELLO".getBytes()
    }

    @SpringBootApplication
    @Import(TestChannelBinderConfiguration)
    static class MyTestConfiguration {
        @Bean
        Function<String, String> uppercase() {
            return { v -> v.toUpperCase() }
        }
    }
}
