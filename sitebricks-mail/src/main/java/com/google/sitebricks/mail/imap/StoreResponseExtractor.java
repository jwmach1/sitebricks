package com.google.sitebricks.mail.imap;

import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jochen@pedesis.org (Jochen Bekmann)
 */
public class StoreResponseExtractor implements Extractor<EnumSet<Flag>> {
  private static final Pattern FETCH_PATT = Pattern.compile(".* FETCH +\\(([^)]+)\\)");

  private static final Pattern BAD_PATT = Pattern.compile("\\d+ +BAD(.*)");
  private static final Pattern NO_PATT = Pattern.compile("\\d+ +NO(.*)");
  private static final Pattern OK_PATT = Pattern.compile("\\d+ +OK(.*)");

  private enum State {
    INIT, GOT_FETCH
  }

  /**
   * Parse the response, which includes new flag settings and command status.
   * We expect only one FETCH response as we only set flags on one msg.
   * 
   * http://tools.ietf.org/html/rfc3501#section-6.4.6
   *   C: A003 STORE 6 +FLAGS (\Deleted)
   *   S: * 4 FETCH (FLAGS (\Deleted \Flagged \Seen) UID 6)
   *   S: A003 OK STORE completed

   */
    @Override
    public EnumSet<Flag> extract(List<String> messages) throws ExtractionException {
      System.out.println("###jochen: messages: " + messages.size());

      State state = State.INIT;
      EnumSet<Flag> result = EnumSet.noneOf(Flag.class);
      ListIterator<String> iterator = messages.listIterator();

      String message = iterator.next();
      System.out.println("STORE RESPONSE: " + message);

      // find FETCH.
      String fetchStr = null;
      for (int i = 0, messagesSize = messages.size(); i < messagesSize; i++) {
        String message = messages.get(i);
        if (null == message || message.isEmpty())
          continue;
        System.out.println("STORE RESPONSE: " + message);

        if(fetchStr == null) {
          String fetchStr = matchAndGetGroup1(FETCH_PATT, message);
          //### parse FLAGS from fetchSTR;
          System.out.println("FLAGS: " + fetchStr);
        } else {
          if(matchAndGetGroup1(OK_PATT, message) != null) {
            if (fetchStr == null) {
              thrown new ExtractionException("STORE RESPONSE: ")
            }
          }

        }





        throw new ExtractionException("STORE failed: " + message);
//        // Discard the success token.
//        if (Command.isEndOfSequence(message))
//          continue;
//
//        // Appears that this message got split between lines. So unfold.
//        if (!message.endsWith(")")) {
//          String next = messages.get(i + 1);
//          message = message + '\n' + next;
//
//          // Skip next.
//          i++;
//        }

//        statuses.add(parseStatus(message.replaceFirst("^[*] ", "")));
      }

//      return statuses;

        return null; //###jochen
    }

    private String matchAndGetGroup1(Pattern p, String s) {
      Matcher m = p.matcher();
      if (m.matches() && m.groupCount() > 1) {
        return m.group(1);
      }
      return null;
    }
}
