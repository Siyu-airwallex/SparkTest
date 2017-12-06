package wordcount

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by charlie on 19/11/17.
 */
class WordCountTest extends Specification {

    WordCountJava wc

    def setup(){
        wc = new WordCountJava()
    }

    @Unroll
    def "load word count file"(){

        wc.run("src/resources/words")
    }
}
