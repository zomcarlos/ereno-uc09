package br.ufu.facom.ereno.attacks.uc09.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

public class OrientedGrayHoleCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;
    Integer discardRate;
    Integer toDiscard;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     */

    public OrientedGrayHoleCreator(ArrayList<Goose> legitimateMessages, int discardRate, int toDiscard) {
        this.legitimateMessages = legitimateMessages;
        this.discardRate = discardRate;
        this.toDiscard = toDiscard;
    }

    /**
     * Discards messages when StNum changes
     */

    Goose previousMessage = null;

    @Override
    public void generate(IED ied, int numberOfMessages) {
        ArrayList<Goose> discardedMessages = new ArrayList<>();

        for (int index = 0; index < legitimateMessages.size(); index++) {
            Goose message = legitimateMessages.get(index);

            if (previousMessage != null) {
                if (message.getStNum() != previousMessage.getStNum()) {
                    if (randomBetween(0, 100) < discardRate) {
                        for (int i = 0; i < toDiscard && (index + i) < legitimateMessages.size(); i++) {
                            Goose msgToDiscard = legitimateMessages.get(index + i);
                            discardedMessages.add(msgToDiscard);
                            System.out.println("Message of timestamp " + msgToDiscard.getTimestamp() + " discarded");
                        }
                        legitimateMessages.subList(index, index + toDiscard).clear();

                        message.setLabel("OrientedGrayHole");
                        legitimateMessages.add(message);
                        break;
                    }
                }
            }
            previousMessage = message; // Update previousMessage
        }
        System.out.println("Discarded " + discardedMessages.size() + " messages");
    }


}