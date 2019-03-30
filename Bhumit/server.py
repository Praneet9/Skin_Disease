from flask import Flask, request, jsonify
import os
from datetime import datetime
import numpy as np

app = Flask(__name__)
UPLOAD_FOLDER = './UPLOAD_FOLDER/'

@app.route('/test')
def hello():
    return 'Hello'

@app.route('/image/upload', methods=['POST'])
def get_image():
    filename = UPLOAD_FOLDER + str(np.random.randint(0, 5000)) + '.png'
    print('Image is incoming')
    photo = request.files['photo']
    photo.save(filename)
    print('Image Saved..')
    return jsonify({'status': True})


if __name__ == '__main__':
    app.run(port=8000)