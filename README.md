## Skin Disease Detection

### Problem Statement at UnScript2k19 Hackathon

#### Output Screenshots
<h5>Case - 1: In case of some Skin Disorder</h5>
<span><img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/3.png" width="25%" height="25%">
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/1.png" width="25%" height="25%">
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/5.png" width="25%" height="25%"></span>
<span><img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/2.png" width="25%" height="25%">
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/4.png" width="25%" height="25%"></span>

<h5>Case - 2: In case of healthy skin and no disorder</h5>
<span>
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/healthy_skin1.png" width="25%" height="25%">
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/healthy_skin2.png" width="25%" height="25%">
</span>

<h5>Case - 3: In case of No Skin Detected</h5>
<span>
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/skin_no_skin1.png" width="25%" height="25%">
<img src="https://github.com/Praneet9/Skin_Disease/blob/master/Bot/output/skin_no_skin2.png" width="25%" height="25%">
</span>

#### Requirements
- Tensorflow-gpu 1.13.0
- Keras 2.2.4
- OpenCV 4.0
- Numpy 1.16.1
- Python-telegram-bot 11.1.0
- Flask

#### Download Model
```
https://drive.google.com/open?id=1es4Oji_651rxAgKJaiDoTiwfJDpwG7fR
```

#### Run telegram bot
```
cd Bot
python scripts/remedium_bot.py
```

#### Launch Flask Server for API
```
cd Bot
python server.py
```
