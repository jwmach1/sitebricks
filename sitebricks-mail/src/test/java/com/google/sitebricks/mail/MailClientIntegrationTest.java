package com.google.sitebricks.mail;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.sitebricks.mail.Mail.Auth;
import com.google.sitebricks.mail.imap.Flag;
import com.google.sitebricks.mail.imap.Folder;
import com.google.sitebricks.mail.imap.FolderStatus;
import com.google.sitebricks.mail.imap.MessageStatus;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class MailClientIntegrationTest {

  static {
    java.util.logging.ConsoleHandler fh = new java.util.logging.ConsoleHandler();
    java.util.logging.Logger.getLogger("").addHandler(fh);
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.FINEST);
  }

  private static final FolderObserver PRINTING_OBSERVER = new FolderObserver() {
    @Override
    public void changed(Set<Integer> added, Set<Integer> removed) {
      System.out.println("New mail arrived!!");
    }
  };

  public static void main(String...args) throws InterruptedException, ExecutionException {
    Mail mail = Guice.createInjector().getInstance(Mail.class);

    
    final MailClient client = mail.clientOf("imap.gmail.com", 993)
        .prepare(Auth.SSL, "wickedwavewrangler@gmail.com", "longboard!"); //###jochen

    client.connect();

    List<String> capabilities = client.capabilities();
    System.out.println("CAPS: " + capabilities);

    final ListenableFuture<FolderStatus> fStatus =
        client.statusOf("[Gmail]/All Mail");
    ListenableFuture<Folder> future = client.open("[Gmail]/All Mail", true);
    final Folder allMail = future.get();
    final FolderStatus folderStatus = fStatus.get();
    System.out.println("Folder opened: " + allMail.getName() + " with count " + folderStatus.getMessages());

    future.addListener(new Runnable() {
      @Override
      public void run() {
//        client.watch(allMail, PRINTING_OBSERVER);

        // Can't send other commands over the channel while idling.
//        client.listFolders();

        ListenableFuture<List<MessageStatus>> messagestatus = client.list(allMail, 1, 1000);

//        ListenableFuture<List<Message>> messages = client.fetch(allMail, 1, 100000);
        try {
          for (MessageStatus status : messagestatus.get()) {
            System.out.println(ToStringBuilder.reflectionToString(status));
          }
          client.addFlags(EnumSet.of(Flag.SEEN), messagestatus.get().get(2).getImapUid());
          //client.send()
          System.out.println("Fetched: " + messagestatus.get().size());
Thread.sleep(2000);
//          for (Message message : messages.get()) {
//            System.out.println(ToStringBuilder.reflectionToString(message));
//            for (Message.BodyPart bodyPart : message.getBodyParts()) {
//              System.out.println(ToStringBuilder.reflectionToString(bodyPart));
//            }
//          }
//
//          for (Message msg : messages.get()) {
//            System.out.println(ToStringBuilder.reflectionToString(msg));
//          }
//
//          System.out.println("Fetched: " + messages.get().size());
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
        client.disconnect();

        System.exit(0);
      }
    }, Executors.newCachedThreadPool());


  }
}
