from glob import glob
import cv2
import numpy as np 
# from skin_detection import get_hist, check_skin
# img_list = sorted(glob('./imgs/*.jpg'))

# for img in img_list:
#     hist = get_hist(img)
#     check = check_skin(hist)
#     if check:
#         print(img, ':Skin')
#     else:
#         print(img, ':Not Skin')

from predict import predict_class, classes

imgs_list = ['03DermatitisArm.jpg']
            #  '03eczema091205.jpg',
            #  '04acidBurn89-GP3.jpg',
            #  'chapped-fissured-feet-34.jpg']

for img in imgs_list:
    dict_dis = predict_class('./imgs/' + img)
    dict_dis = sorted(dict_dis.items(), key=lambda x: x[1], reverse=True)
    print(dict_dis[:3])