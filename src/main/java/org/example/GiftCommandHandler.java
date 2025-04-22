package org.example;

import java.util.List;

public class GiftCommandHandler {

    private final DatabaseHandler dbHandler;
    private final CommandHandler mainCommandHandler;

    private String tempGiftName;
    private int tempPersonId;
    private List<Gift> giftList;
    private List<Birthday> birthdaysList;


    public GiftCommandHandler(DatabaseHandler dbHandler, CommandHandler mainCommandHandler) {
        this.dbHandler = dbHandler;
        this.mainCommandHandler = mainCommandHandler;
    }

    public String handleAddGiftCommand(long chatId) {
        birthdaysList = dbHandler.getAllBirthdays(String.valueOf(chatId));
        if (birthdaysList.isEmpty()) {
            mainCommandHandler.setBotState(State.IDLE);
            return "Список дней рождений пустой! Сначала добавьте день рождения!";
        }

        StringBuilder ownerListBuilder = new StringBuilder("Список дней рождений: \n");
        for (int i = 0; i < birthdaysList.size(); i++) {
            ownerListBuilder.append((i + 1)).append(".").append(birthdaysList.get(i).getName()).append("\n");
        }

        ownerListBuilder.append("Введите номер человека, которому хотите добавить подарок: ");
        mainCommandHandler.setBotState(State.AWAITING_GIFT_OWNER_NUMBER);
        return ownerListBuilder.toString();
    }

    public String handleAwaitingGiftOwnerNumber(String messageText, long chatId) {
        try {
            int ownerIndex;
            ownerIndex = Integer.parseInt(messageText) - 1;

            if (ownerIndex >= 0 && ownerIndex < birthdaysList.size()) {
                tempPersonId = birthdaysList.get(ownerIndex).getId();
                String tempName = birthdaysList.get(ownerIndex).getName();
                mainCommandHandler.setBotState(State.AWAITING_GIFT_NAME);
                System.out.println("Выбран ID: " + tempPersonId + " для " + tempName);
                return "Введите название подарка для " + tempName + ":";
            } else {
                mainCommandHandler.setBotState(State.AWAITING_GIFT_OWNER_NUMBER);
                return "Неверный номер имени. Введите номер из списка.";
            }
        } catch (NumberFormatException e) {
            mainCommandHandler.setBotState(State.AWAITING_GIFT_OWNER_NUMBER);
            return "Неверный номер имени. Введите номер из списка.";
        }
    }

    public String handleAwaitingGiftName(String messageText, long chatId) {
        tempGiftName = messageText.trim();
        dbHandler.addGift(tempGiftName, tempPersonId, String.valueOf(chatId));
        mainCommandHandler.setBotState(State.IDLE);
        return "Подарок '" + tempGiftName + "' добавлен!";
    }

    public String handleRemoveGiftCommand(long chatId) {
        birthdaysList = dbHandler.getAllBirthdays(String.valueOf(chatId));
        if (birthdaysList.isEmpty()) {
            mainCommandHandler.setBotState(State.IDLE);
            return "Список дней рождений пустой! Сначала добавьте день рождения!";
        }

        StringBuilder ownerListBuilder = new StringBuilder("Список дней рождений: \n");
        for (int i = 0; i < birthdaysList.size(); i++) {
            ownerListBuilder.append((i + 1)).append(". ").append(birthdaysList.get(i).getName()).append("\n");
        }

        ownerListBuilder.append("Введите номер человека, у которого хотите удалить подарок: ");
        mainCommandHandler.setBotState(State.AWAITING_GIFT_OWNER_NUMBER_TO_REMOVE);
        return ownerListBuilder.toString();
    }

    public String handleRemoveGiftAwaitingOwnerNumber(String messageText, long chatId) {
        int ownerIndex;
        ownerIndex = Integer.parseInt(messageText)-1;

        if (ownerIndex >= 0 && ownerIndex < birthdaysList.size()) {
            tempPersonId = birthdaysList.get(ownerIndex).getId();

            giftList = dbHandler.getGiftsForPerson(tempPersonId, String.valueOf(chatId));

            if (giftList.isEmpty()){
                mainCommandHandler.setBotState(State.IDLE);
                return "Список подарков пуст!";
            } else {
                StringBuilder giftListBuilder = new StringBuilder("Список подарков: \n");
                for (int i = 0; i < giftList.size(); i++) {
                    giftListBuilder.append((i + 1)).append(". ").append(giftList.get(i).getName()).append("\n");
                }
                mainCommandHandler.setBotState(State.AWAITING_GIFT_NUMBER_TO_REMOVE);
                return giftListBuilder.toString() + "Введите номер подарка для удаления: ";
            }
        } else {
            mainCommandHandler.setBotState(State.IDLE);
            return "Неверный номер имени.";
        }
    }

    public String handleAwaitingGiftNumberToRemove(String messageText, long chatId) {
        int giftIndex;
        try {
            giftIndex = Integer.parseInt(messageText) - 1;

            if (giftIndex >= 0 && giftIndex < giftList.size()) {
                String tempGiftName = giftList.get(giftIndex).getName();
                dbHandler.removeGift(tempGiftName, tempPersonId, String.valueOf(chatId));
                mainCommandHandler.setBotState(State.IDLE);
                return "Подарок " + tempGiftName + " удален из списка подарков!";
            } else {
                mainCommandHandler.setBotState(State.IDLE);
                return "Неверный номер подарка.";
            }
        } catch (NumberFormatException e) {
            mainCommandHandler.setBotState(State.IDLE);
            return "Неверный формат номера. Введите номер из списка.";
        }
    }

    public String handleViewGiftCommand(long chatId) {
        birthdaysList = dbHandler.getAllBirthdays(String.valueOf(chatId));
        if (birthdaysList.isEmpty()) {
            mainCommandHandler.setBotState(State.IDLE);
            return "Список дней рождений пустой! Сначала добавьте день рождения!";
        }

        StringBuilder ownerListBuilder = new StringBuilder("Список дней рождений: \n");
        for (int i = 0; i < birthdaysList.size(); i++) {
            ownerListBuilder.append((i + 1)).append(". ").append(birthdaysList.get(i).getName()).append("\n");
        }

        ownerListBuilder.append("Введите номер человека, у которого хотите посмотреть список подарков: ");
        mainCommandHandler.setBotState(State.AWAITING_GIFT_OWNER_NUMBER_TO_VIEW);
        return ownerListBuilder.toString();
    }

    public String handleAwaitingGiftOwnerNumberForView(String messageText, long chatId) {
        int ownerIndex;
        ownerIndex = Integer.parseInt(messageText)-1;

        if (ownerIndex >= 0 && ownerIndex < birthdaysList.size()) {
            tempPersonId = birthdaysList.get(ownerIndex).getId();

            giftList = dbHandler.getGiftsForPerson(tempPersonId, String.valueOf(chatId));

            if (giftList.isEmpty()){
                mainCommandHandler.setBotState(State.IDLE);
                return "Список подарков пуст!";
            } else {
                StringBuilder giftListBuilder = new StringBuilder("Список подарков: \n");
                for (int i = 0; i < giftList.size(); i++) {
                    giftListBuilder.append((i + 1)).append(".").append(giftList.get(i).getName()).append("\n");
                }

                return giftListBuilder.toString();
            }
        } else {
            mainCommandHandler.setBotState(State.IDLE);
            return "Неверный номер имени.";
        }
    }

}
