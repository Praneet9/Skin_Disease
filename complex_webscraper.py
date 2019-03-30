# Web Scraper(DermNetNZ)
# By: Adham Elarabawy
# Date: 4/27/2018
import os
import time
import requests
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from PIL import Image

#quick change:
#topic_list = ['acne', 'carcinoma', 'varicella', 'eczema', 'urticaria', 'melanoma', 'psoriasis', 'rosacea', 'wart', 'impetigo', 'mole', 'dermatitis', 'herpes simplex', 'herpes zoster', 'tinea pedis', 'insect']
buffer_time = 2                            #debug purposes

def imgDownload(url, dwn_path):            #function for downloading images from url and destination path
    try:
        r = requests.get(url, stream=True)
        if r.status_code == 200:
            with open(dwn_path + ".png", "wb") as f:
                for chunk in r:
                    f.write(chunk)
        im = Image.open(dwn_path+".png")
        im.convert('RGB').save(dwn_path + ".jpg","JPEG") #this converts png image as jpeg
        os.remove(dwn_path + '.png')
    except Exception as e:
        print("Exception occured")
        print(e)

def imgCrop(dwn_path_c, dwn_path_o):       #function for cropping image(top-left) from image path
    temp1 = Image.open(dwn_path_o + ".jpg")
    temp2 = temp1.crop((0, 0, crop_size, crop_size))
    temp2.save(dwn_path_c + cropped_suffix + ".jpg")

options = webdriver.ChromeOptions()
# options.add_argument('headless')
driver = webdriver.Chrome(chrome_options=options) #using selenium to obtain RENDERED html & easily navigate it
driver.get("https://www.dermnetnz.org/image-library/");
html = driver.execute_script("return document.body.innerHTML")

print("Buffer time for page to load:")
for x in range(1, buffer_time + 1):        #Buffer time for page to fully load/render
    print(x)
    time.sleep(1)
print("Done Buffering...")




counter = 0
index = 0
list_of_letters = driver.find_elements_by_class_name("imageList")
for letter in list_of_letters:
    print("\tLETTER:", letter.find_element_by_class_name("imageList__title").text)
    list_in_letter = letter.find_element_by_class_name("imageList__group").find_element_by_class_name("flex")
    list_of_sections = list_in_letter.find_elements_by_class_name('imageList__group__item')

    for section in list_of_sections:
        print("\t\tsection:", section.text)
        topic = section.find_element_by_class_name('imageList__group__item__copy').text.replace('images', '').replace('/', '').strip()
        section.send_keys(Keys.CONTROL + Keys.RETURN)
        driver.switch_to_window(driver.window_handles[1])
        list_of_images = driver.find_elements_by_class_name('imageLinkBlock__item__image')

    #     for element in driver.find_elements_by_class_name("imageList__group__item"):
    # img_link = element.find_element_by_class_name("imageList__group__item__image").find_element_by_tag_name("img").get_attribute("src")

        for image in list_of_images:
            counter += 1
            index += 1
            img_link = image.find_element_by_tag_name("img").get_attribute("src")
            img_name = topic + str(index)
            print("URL:", img_link)
            print("#:", counter)
            if not os.path.exists("./storedImages/" + topic):
                os.makedirs("./storedImages/" + topic)
            dwn_path = "./storedImages/" + topic + "/" + img_name
            if os.path.exists(dwn_path + '.png'):
                continue
            imgDownload(img_link, dwn_path)
        driver.close()
        driver.switch_to_window(driver.window_handles[0])

print("Done scraping... [" + str(counter) + "] images have been successfully downloaded")
driver.quit()