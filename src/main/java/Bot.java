import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ApproveChatJoinRequest;
import org.telegram.telegrambots.meta.api.methods.groupadministration.DeclineChatJoinRequest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "test_tg_robot";
    }

    @Override
    public String getBotToken() {
        return "5655012675:AAG6YOkNfARF8xewLj0KqQrjh7i6cZfdQow";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasChatJoinRequest()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("\uD83D\uDD30 Для вступления в группу \""+update.getChatJoinRequest().getChat().getTitle()+"\" пройдите капчу. Нажмите на самолёт!");
            sendMessage.setChatId(String.valueOf(update.getChatJoinRequest().getUser().getId()));
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> all = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("✈️");
                button.setCallbackData("true="+update.getChatJoinRequest().getChat().getId());
                row.add(button);
            }
            {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("\uD83D\uDE98");
                button.setCallbackData("false="+update.getChatJoinRequest().getChat().getId());
                row.add(button);
            }
            {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("\uD83D\uDE9C");
                button.setCallbackData("false="+update.getChatJoinRequest().getChat().getId());
                row.add(button);
            }
            all.add(row);
            inlineKeyboardMarkup.setKeyboard(all);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else if(update.hasCallbackQuery()){
            String data = update.getCallbackQuery().getData();
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
            deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            if(data.substring(0,data.indexOf("=")).equals("true")){
                ApproveChatJoinRequest approveChatJoinRequest = new ApproveChatJoinRequest();
                approveChatJoinRequest.setUserId(update.getCallbackQuery().getFrom().getId());
                approveChatJoinRequest.setChatId(data.substring(data.indexOf("=")+1));
                try {
                    execute(approveChatJoinRequest);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getFrom().getId()));
                    sendMessage.setText("✅ Вы приняты в группу");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(data.substring(data.indexOf("=")+1));
                    sendMessage.setText("@"+update.getCallbackQuery().getFrom().getId()+" принят в группу");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                DeclineChatJoinRequest declineChatJoinRequest = new DeclineChatJoinRequest();
                declineChatJoinRequest.setUserId(update.getCallbackQuery().getFrom().getId());
                declineChatJoinRequest.setChatId(data.substring(data.indexOf("=")+1));
                try {
                    execute(declineChatJoinRequest);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getFrom().getId()));
                    sendMessage.setText("❌ Вы не прошли капчу");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
