package br.ufu.facom.ereno.attacks.uc09.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

public class OrientedGrayHoleCreator implements MessageCreator {

    enum attackAproaches {
        BURST,
        RANDOMIC
    }

    ArrayList<Goose> legitimateMessages;
    Integer discardRate;
    Integer toDiscardPackets;

    attackAproaches approaches;

    public OrientedGrayHoleCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
        this.discardRate = 20;
        this.toDiscardPackets = 10;
        this.approaches = attackAproaches.RANDOMIC;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {

        ArrayList<Goose> discardedMessages = new ArrayList<>();
        boolean burstDiscard = false;
        boolean ignoreAnalysis = false;
        int maxDisposableIndex = 0;
        boolean discarded = false;

        for (int i = 1; i < legitimateMessages.size() - 1; i++) {

            Goose message = legitimateMessages.get(i);
            Goose lastMessage = legitimateMessages.get(i - 1);
            Goose nextMessage = legitimateMessages.get(i + 1);

            if (discarded) {
                message.setLabel(GSVDatasetWriter.label[9]);
            }

            if (!ignoreAnalysis && message.getStNum() != lastMessage.getStNum()) {
                switch (approaches) {

                    case RANDOMIC:
                        boolean toDiscard = randomBetween(0, 100) < discardRate;
                        if (toDiscard) {
                            discardedMessages.add(message);
                            discarded = true;
                            System.out.println("Discarded the message " +
                                    "of timestamp " + message.getTimestamp() + "through the " + attackAproaches.RANDOMIC.name() + " approach.");
                        } else {
                            ied.addMessage(message);
                            System.out.println("Avoided discarding the message " +
                                    "of timestamp " + message.getTimestamp());
                        }
                        break;

                    case BURST:
                        burstDiscard = true;
                        maxDisposableIndex = i + toDiscardPackets;
                }
                if (burstDiscard) {
                    discardedMessages.add(message);
                    System.out.println("Discarded the message " +
                            "of timestamp " + message.getTimestamp() + " through the " + attackAproaches.BURST.name() + " approach.");
                    ignoreAnalysis = i <= maxDisposableIndex;
                    burstDiscard = false;
                }
            }

        }
        System.out.println("Discarded " + discardedMessages.size() + " messages");
    }
}