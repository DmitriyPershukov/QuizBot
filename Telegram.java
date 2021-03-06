package com.company;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Telegram extends TelegramLongPollingBot {
    private static HashMap<String, Dialogue> chats;

    public static void init() throws Exception {
        ApiContextInitializer.init();
        Config.setConfig();
        chats = new HashMap<String, Dialogue>();
        TelegramBotsApi botApi = new TelegramBotsApi();
        try {
            botApi.registerBot(new Telegram());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        User user = new User(message);
        if (!chats.containsKey(user.userId)) {
            try {
                chats.put(user.userId, new Dialogue(user.userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Output getData = new Output();
        try {
            getData = chats.get(user.userId).returnQuizAnswer(message.getText());
            if(!(getData.text == null) && getData.text.length() > 0)
            {
                sendMsg(message, getData);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void setButtons(SendMessage sendMessage, String[] answers) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (int i = 0; i < answers.length; i += 2) {
            KeyboardRow row = new KeyboardRow();
            for (int j = i; j < answers.length && j < i + 2; ++j)
                row.add(new KeyboardButton(answers[j]));
            keyboard.add(row);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private synchronized void setText(SendMessage sendMessage, String text) {
        sendMessage.enableMarkdown(true);
        sendMessage.setText(text);
    }

    private synchronized void sendMsg(Message msg, Output data) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());
        //sendMessage.setReplyToMessageId(msg.getMessageId());

        setText(sendMessage, data.text);
        if (data.possibleAnswers != null)
            setButtons(sendMessage, data.possibleAnswers);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return Config.botName;
    }

    @Override
    public String getBotToken() {
        return Config.botToken;
    }
}
