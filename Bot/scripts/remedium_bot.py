from telegram.ext import Updater, CommandHandler, MessageHandler, Filters
import telegram
import logging
import copy
import cv2, os
import numpy as np
from skin_detection import check_skin
from predict import predict_class
import matplotlib.pyplot as plt

content = {}
# Enable logging
logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
                    level=logging.INFO)

logger = logging.getLogger(__name__)


def error(bot, update, error):
    """Log Errors caused by Updates."""
    logger.warning('Update "%s" caused error "%s"', update, error)

def start_handler(bot, update):
    bot.send_message(update.message.chat_id, text='Upload the image of Infected Area.')

def photo_handler(bot, update):
    if update.message.photo != []:
        upd = copy.deepcopy(update.to_dict())
        user_id = update.message.from_user.id
        f_name = update.message.from_user.first_name
        l_name = update.message.from_user.last_name
        content['user_id'] = user_id
        if l_name is None:
            content['name'] = f_name
        else:
            content['name'] = f_name + ' ' + l_name
        content['file_info'] = update.message.photo[-1].get_file()['file_path']
        image = update.message.photo[-1].get_file().download('./temp/' + str(update.message.chat_id) + '.jpg')

        # After Photo ask for location and contact
        location_keyboard = telegram.KeyboardButton(text="send_location", request_location=True)
        contact_keyboard = telegram.KeyboardButton(text="send_contact", request_contact=True)
        custom_keyboard = [[location_keyboard, contact_keyboard]]
        reply_markup = telegram.ReplyKeyboardMarkup(custom_keyboard)
        bot.send_message(chat_id=update.message.chat_id,
                         text="Would you mind sharing your location and contact with us?",
                         reply_markup=reply_markup)

def location_handler(bot, update):
    upd = copy.deepcopy(update.to_dict())
    if upd.get('message').get('location') is not None:
        lat = upd.get('message').get('location').get('latitude')
        lng = upd.get('message').get('location').get('longitude')
        # print(str(lat) + ',' + str(lng))
        content['lat'] = str(lat)
        content['long'] = str(lng)

def contact_handler(bot, update):
    upd = update.to_dict()
    phone_no = str('+') + upd.get('message').get('contact').get('phone_number')
    content['contact'] = phone_no
    
    if check_skin('./temp/' + str(update.message.chat_id) + '.jpg'):
            
        preds_dict = predict_class('./temp/' + str(update.message.chat_id) + '.jpg')

        dict_dis = sorted(preds_dict.items(), key=lambda x: x[1], reverse=True)
        print(dict_dis)
        dict_dis = dict(sorted(preds_dict.items(), key=lambda x: x[1], reverse=True)[:3])
        max_val = max(dict_dis, key=dict_dis.get)
        if dict_dis[max_val] <= 39:
            bot.send_message(chat_id=update.message.chat_id, text='Healthy Skin Detected')
        else:
            plt.bar(range(len(dict_dis)), list(dict_dis.values()), align='center')
            x1,x2,y1,y2 = plt.axis()
            plt.axis((x1,x2,0,100))
            plt.xticks(range(len(dict_dis)), range(len(dict_dis)))
            plt.savefig('./temp/plots/tempfig.png')
            bot.send_message(chat_id=update.message.chat_id, text="Thank you!\n \nYou will recieve your report Soon")
            bot.send_photo(chat_id=update.message.chat_id, photo=open('./temp/plots/tempfig.png', 'rb'))
            os.remove('./temp/plots/tempfig.png')
            plt.cla()
            keys = list(dict_dis.keys())
            message_text = '0 ->' + str(keys[0]) + '\n1 ->' + str(keys[1]) + '\n2 ->' + str(keys[2]) 
            bot.send_message(chat_id=update.message.chat_id, text=message_text)
    else:
        bot.send_message(chat_id=update.message.chat_id, text='Please Upload Infected Skin Area for Diagnosis')

def main():
    TOKEN = open('api_key.txt').read()
    updater = Updater(TOKEN)

    dp = updater.dispatcher

    ## Adding Command Handler and Message Handler
    start_cmd_handler = CommandHandler('start', start_handler)
    photo_msg_handler = MessageHandler(Filters.photo, photo_handler)
    location_msg_handler = MessageHandler(Filters.location, location_handler)
    contact_msg_handler = MessageHandler(Filters.reply, contact_handler)
    dp.add_handler(start_cmd_handler)
    dp.add_handler(photo_msg_handler)
    dp.add_handler(location_msg_handler)
    dp.add_handler(contact_msg_handler)
    # log all errors
    dp.add_error_handler(error)

    updater.start_polling()

    updater.idle()



if __name__ == "__main__":
    main()
    content = {}