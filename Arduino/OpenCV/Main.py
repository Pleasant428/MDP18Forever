import cv2
import imutils
import numpy as np

cap = cv2.VideoCapture(0)
# files = {"template_100.png", "template_90.png", "template_80.png", "template_70.png", "template_60.png",
#          "template_50.png", "template_40.png", "template_30.png", "template_20.png"}
files = {"template_80.png", "template_70.png", "template_60.png",
         "template_50.png", "template_40.png", "template_30.png", "template_20.png"}
size = [0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]
temp = []
# for file in files:
# temp.append(cv2.Canny(cv2.imread(file, cv2.IMREAD_GRAYSCALE), 100, 200))
# temp.append(cv2.imread(file, cv2.IMREAD_GRAYSCALE))
# temp.append(cv2.adaptiveThreshold(cv2.imread(file, cv2.IMREAD_GRAYSCALE), 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, \
#                                   cv2.THRESH_BINARY, 11, 2))

orig = cv2.imread("template_100.png", cv2.IMREAD_GRAYSCALE)
for n, s in enumerate(size):
    resize = imutils.resize(orig, width=int(orig.shape[1] * s))
    temp.append(cv2.Canny(resize, 100, 200))
    cv2.imshow("frame " + str(n), temp[n])
    # temp.append(resize)
    # temp.append(cv2.adaptiveThreshold(resize, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, \
    #                                   cv2.THRESH_BINARY, 11, 2))
while (True):
    # Capture frame-by-frame
    ret, frame = cap.read()

    # Our operations on the frame come here
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    # gray = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, \
    #                              cv2.THRESH_BINARY, 11, 2)
    gray = cv2.Canny(gray, 100, 200)
    img3 = gray

    top_left_list = []
    bottom_right_list = []
    max_val_list = []
    for n, t in enumerate(temp):
        w, h = t.shape[::-1]
        try:
            res = cv2.matchTemplate(gray, t, cv2.TM_CCOEFF_NORMED)
        except:
            max_val_list.append(-1)
            top_left_list.append(-1)
            bottom_right_list.append(-1)
            continue
        min_val, max_val, min_loc, max_loc = cv2.minMaxLoc(res)
        print("Confidence level: " + str(max_val))
        if max_val < 0.35:
            max_val_list.append(-1)
            top_left_list.append(-1)
            bottom_right_list.append(-1)
            continue
        else:
            # print("Confidence level: " + str(max_val))
            # print("Size: " + str(n))
            top_left = max_loc
            bottom_right = (top_left[0] + w, top_left[1] + h)
            max_val_list.append(max_val)
            top_left_list.append(top_left)
            bottom_right_list.append(bottom_right)

    if max(max_val_list) != -1:
        img3 = cv2.rectangle(gray, top_left_list[np.asarray(max_val_list).argmax()],
                             bottom_right_list[np.asarray(max_val_list).argmax()], (0, 0, 255), 5)
    cv2.imshow('frame', img3)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
