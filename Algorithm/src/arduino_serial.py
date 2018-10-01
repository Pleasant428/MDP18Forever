import serial
import time

class Ar_Sr(object):

        def __init__(self):
                self.port = '/dev/ttyACM0'
                self.baud_rate = 9600


        def connect(self):
                try:
                        self.serial_sock = serial.Serial(self.port, self.baud_rate)
                        print "Connection established..."

                except Exception, e:
                        print "Serial Connection Exception: %s" %str(e)


        def disconnect(self):
                try:
                        self.serial_sock.close()
                        print "Closing serial socket"

                except Exception, e:
                        print "Serial Disconnection Exception: %s" %str(e)


        def arduino_read(self):
                try:
                        data = self.serial_sock.readline()
                        return data

                except Exception, e:
                        print "Arduino Read Exception: %s" %str(e)


        def arduino_write(self, data):
                try:
                self.serial_sock.write(data)

                except Exception, e:
                        print "Arduino Write Exception: %s" %str(e)


if __name__ == "__main__":
        print "ruuuuuunnnn"
        sr = Ar_Sr()
        sr.connect()
        print "Serial Connection Established 2"

        try:
                while 1:
                        #print "read"
                        #print "data received: %s" % sr.arduino_read()
                        x = raw_input()
                        sr.arduino_write(x)
                        print("sent")
                        print(sr.arduino_read())
                except Exception, e:
                        print "Arduino Write Exception: %s" %str(e)


if __name__ == "__main__":
        print "ruuuuuunnnn"
        sr = Ar_Sr()
        sr.connect()
        print "Serial Connection Established 2"

        try:
                while 1:
                        #print "read"
                        #print "data received: %s" % sr.arduino_read()
                        x = raw_input()
                        sr.arduino_write(x)
                        print("sent")
                        print(sr.arduino_read())

        except KeyboardInterrupt:
                sr.disconnect()
