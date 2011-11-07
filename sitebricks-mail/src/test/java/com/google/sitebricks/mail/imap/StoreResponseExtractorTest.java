package com.google.sitebricks.mail.imap;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.fail;


/**
 * @author jochen@pedesis.org (Jochen Bekmann)
 */
public class StoreResponseExtractorTest {
  private static final List<String> BAD_RESPONSE = ImmutableList.of("* 66 BAD stupid stuff.");
  private static final List<String> NO_RESPONSE = ImmutableList.of("* 66 NO meh, you can't");
  private StoreResponseExtractor extractor;

  @BeforeTest
  public final void setup() {
    extractor = new StoreResponseExtractor();
  }

  @Test
  public final void testBadResponse() {
    try {
      extractor.extract(BAD_RESPONSE);
      fail("Expected BAD response to fail.");
    }
    catch (ExtractionException ee) {
      // expected.
    }
  }

  @Test
  public final void testErrorResponse() {
    try {
      extractor.extract(NO_RESPONSE);
      fail("Expected BAD response to fail.");
    }
    catch (ExtractionException ee) {
      // expected.
    }
  }

}
