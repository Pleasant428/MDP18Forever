import cv2
import numpy as np

cap = cv2.VideoCapture(0)

template = cv2.imread('template_20.png',0)
w, h = template.shape[::-1]

while(cap.isOpened()):         
    ret, frame = cap.read()
    print(ret)
    # img_rgb = cv2.imread('buoys.jpg')
    img_rgb = frame
    img_gray = cv2.cvtColor(img_rgb, cv2.COLOR_BGR2GRAY)

    img_gray = cv2.bilateralFilter(img_gray, 9, 75, 75)

    # Scale frame up to 1920x1080
    width, height = img_gray.shape[::-1]
    # img_gray = cv2.resize(img_gray, dsize=None, fx=1/np.sqrt(2), fy=1/np.sqrt(2))
    img_gray = cv2.resize(img_gray, None, fx=1920/width, fy=1080/height, interpolation = cv2.INTER_LINEAR)
    start_width, start_height = img_gray.shape[::-1]
    width = start_width
    height = start_height
    while(width > 200):
        img_gray = cv2.resize(img_gray, dsize=None, fx=1/np.sqrt(2), fy=1/np.sqrt(2))
        width, height = img_gray.shape[::-1]
        res1 = cv2.matchTemplate(img_gray, template, cv2.TM_CCOEFF_NORMED)
        # print(np.max(res1))
        threshold = 0.80
        loc = np.where( res1 >= threshold)

        scale = start_width / width
        for pt in zip(*loc[::-1]):
            cv2.rectangle(img_rgb, (int(pt[0]*scale), int(pt[1]*scale)), (int((pt[0] + w)*scale), int((pt[1] + h)*scale)), (0,0,255), 2)

    cv2.imshow('frame',img_rgb)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()