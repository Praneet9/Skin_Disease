from flask import Flask, request, jsonify
import os
from datetime import datetime
import numpy as np
from ignore.skin_detection import check_skin
from ignore.predict import predict_class
import cv2


app = Flask(__name__)
UPLOAD_FOLDER = './UPLOAD_FOLDER/'

@app.route('/test')
def hello():
    return 'Hello'

@app.route('/image/upload', methods=['POST', 'GET'])
def get_image():
    filename = UPLOAD_FOLDER + str(np.random.randint(0, 5000)) + '.png'
    print('Image is incoming')
    photo = request.files['photo']
    photo.save(filename)
    print('Image Saved..')
    if check_skin(filename):
        preds_dict = predict_class(filename)
        
        dict_dis = sorted(preds_dict.items(), key=lambda x: x[1], reverse=True)
        dict_dis = dict(sorted(preds_dict.items(), key=lambda x: x[1], reverse=True)[:3])
        print(dict_dis)

        max_val = max(dict_dis, key=dict_dis.get)
        if dict_dis[max_val] <= 38:
            print('healthy')
            return jsonify({'message':'Healthy Skin Detected'})
        else:
            print('Done')
            return jsonify({'message': str(max_val),'percentage': str(dict_dis[max_val])})
    else:
        print({'message': 'Please upload image of Infected Area'})
        return jsonify({'message': 'Please upload image of Infected Area'})


if __name__ == '__main__':
    app.run(port=8000)