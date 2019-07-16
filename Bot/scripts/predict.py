from keras.models import load_model
import tensorflow as tf
import cv2
global graph, sess

classes = {0:'Acne/Rosacea',
           1:'Actinic Keratosis/Basal Cell Carcinoma/Malignant Lesions',
           2:'Eczema',
           3:'Melanoma Skin Cancer/Nevi/Moles',
           4:'Psoriasis/Lichen Planus and related diseases', 
           5:'Tinea Ringworm/Candidiasis/Fungal Infections',
           6:'Urticaria/Hives', 
           7:'Nail Fungus/Nail Disease'}

graph = tf.Graph()
sess = tf.Session(graph = graph)

with graph.as_default():
    with sess.as_default():
        model = load_model('P:/Hackathons/Unscript_2k19/Skin_Disease/Bhumit/ignore/final_vgg1920epochs.h5', compile=True)

def predict_class(image):
    img = cv2.imread(image)
    img = cv2.resize(img, (32,32)) / 255.0
    with graph.as_default():
        with sess.as_default():
            predictions = (model.predict(img.reshape(1,32,32,3)) * 100.0).round(2) 
    new_dict = {
        classes[0]: predictions[0][0],
        classes[1]: predictions[0][1],
        classes[2]: predictions[0][2],
        classes[3]: predictions[0][3],
        classes[4]: predictions[0][4],
        classes[5]: predictions[0][5],
        classes[6]: predictions[0][6],
        classes[7]: predictions[0][7]
    }

    return new_dict