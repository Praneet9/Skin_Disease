import cv2
import numpy as np
from matplotlib import pyplot as plt
#Open a simple image

def get_hist(image_path):
    img=cv2.imread(image_path)
    img = cv2.resize(img, (256,256))

    #converting from gbr to hsv color space
    img_HSV = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    #skin color range for hsv color space 
    HSV_mask = cv2.inRange(img_HSV, (0, 15, 0), (17,170,255)) 
    HSV_mask = cv2.morphologyEx(HSV_mask, cv2.MORPH_OPEN, np.ones((3,3), np.uint8))

    #converting from gbr to YCbCr color space
    img_YCrCb = cv2.cvtColor(img, cv2.COLOR_BGR2YCrCb)
    #skin color range for hsv color space 
    YCrCb_mask = cv2.inRange(img_YCrCb, (0, 135, 85), (255,180,135)) 
    YCrCb_mask = cv2.morphologyEx(YCrCb_mask, cv2.MORPH_OPEN, np.ones((3,3), np.uint8))

    #merge skin detection (YCbCr and hsv)
    global_mask=cv2.bitwise_and(YCrCb_mask,HSV_mask)
    global_mask=cv2.medianBlur(global_mask,3)
    global_mask = cv2.morphologyEx(global_mask, cv2.MORPH_OPEN, np.ones((4,4), np.uint8))


    HSV_result = cv2.bitwise_not(HSV_mask)
    YCrCb_result = cv2.bitwise_not(YCrCb_mask)
    global_result=cv2.bitwise_not(global_mask)
    # final = cv2.bitwise_and(img, global_result)

    #show results
    # cv2.imshow("1_HSV.jpg",HSV_result)
    # cv2.imshow("2_YCbCr.jpg",YCrCb_result)
    # cv2.imshow("3_global_result.jpg",global_result)
    # cv2.imshow("Final_Image",final)
    # cv2.imwrite("1_HSV.jpg",HSV_result)
    # cv2.imwrite("2_YCbCr.jpg",YCrCb_result)
    cv2.imwrite("temp.jpg",global_result)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()  

    # img2 = cv2.imread('temp.jpg')
    # final = cv2.bitwise_and(img2, img)
    # cv2.imwrite('final_temp.jpg', final)
    # final = cv2.cvtColor(final, cv2.COLOR_BGR2GRAY)
    hist = cv2.calcHist([global_result],[0],None,[256],[0,256])
    # print(hist[0], hist[255])
    # plt.hist(global_result.ravel(),256,[0,256])
    # plt.title('Histogram for gray scale picture')
    # plt.show()
    return hist

def check_skin(image_name):
    hist = get_hist(image_name)
    a = hist[0]
    b = hist[255]
    percent = ((a / (a + b)) * 100.0).round(2)

    if percent > 5.00:
        return True
    else:
        return False