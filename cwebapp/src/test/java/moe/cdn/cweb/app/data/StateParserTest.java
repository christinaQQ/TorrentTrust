package moe.cdn.cweb.app.data;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

/**
 * @author davix
 */
public class StateParserTest {
    @Test
    public void testParseInitialState() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(State.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        StreamSource jsonStream =
                new StreamSource(StateParserTest.class.getResourceAsStream("initial_state.json"));
        State state = unmarshaller.unmarshal(jsonStream, State.class).getValue();

        fail("unimplemented");
    }
}