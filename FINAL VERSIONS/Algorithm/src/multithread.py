from android_bluetooth import *
from arduino_serial import *
from pc_wifi import *

import Queue
import threading
import time

class myThread(threading.Thread):
        def __init__(self):
                threading.Thread.__init__(self)
                self.android_thread = An_BT()
                self.arduino_thread = Ar_Sr()
                self.pc_thread = Pc_Wf()

                self.android_thread.connect()
                self.arduino_thread.connect()
                self.pc_thread.connect()

                self.android_queue = Queue.Queue(maxsize=0)
                self.arduino_queue = Queue.Queue(maxsize=0)
                self.pc_queue = Queue.Queue(maxsize=0)

                time.sleep(1)

        def read_an(self, pc_queue):
                while 1:
                        data = self.android_thread.android_read()
                        pc_queue.put_nowait(data)
                        print "from ANDROID to PC: %s\n" %data

        def write_an(self, android_queue):
                while 1:
                        if not android_queue.empty():
                                data = android_queue.get_nowait()
                                self.android_thread.android_write(data)
                                print "to ANDROID: %s\n" %data

        def read_ar(self, pc_queue):
                self.arduino_queue = Queue.Queue(maxsize=0)
                self.pc_queue = Queue.Queue(maxsize=0)

                time.sleep(1)

        def read_an(self, pc_queue):
                while 1:
                        data = self.android_thread.android_read()
                        pc_queue.put_nowait(data)
                        print "from ANDROID to PC: %s\n" %data

        def write_an(self, android_queue):
                while 1:
                        if not android_queue.empty():
                                data = android_queue.get_nowait()
                                self.android_thread.android_write(data)
                                print "to ANDROID: %s\n" %data

        def read_ar(self, pc_queue):
                while 1:
                        data = self.arduino_thread.arduino_read()
                        pc_queue.put_nowait(data)
                        print "from ARDUINO to PC: %s\n" %data

        def write_ar(self, arduino_queue):
                while 1:
                        if not arduino_queue.empty():
                                data = arduino_queue.get_nowait()
                                self.arduino_thread.arduino_write(data)
                                print "to ARDUINO: %s\n" %data


        def read_pc(self, arduino_queue, android_queue):
                while 1:
                        data = self.pc_thread.pc_read()
                        if (data[0].lower() == 'r'):
                                arduino_queue.put_nowait(data[1:])
                                print "from PC to ARDUINO: %s\n" % data[1:]
                        elif (data[0].lower() == 'n'):
                                 data = self.arduino_thread.arduino_read()
                                 pc_queue.put_nowait(data)
                                 print "from ARDUINO to PC: %s\n" %data

        def write_ar(self, arduino_queue):
                while 1:
                        if not arduino_queue.empty():
                                data = arduino_queue.get_nowait()
                                self.arduino_thread.arduino_write(data)
                                print "to ARDUINO: %s\n" %data


        def read_pc(self, arduino_queue, android_queue):
                while 1:
                        data = self.pc_thread.pc_read()
                        if (data[0].lower() == 'r'):
                                arduino_queue.put_nowait(data[1:])
                                print "from PC to ARDUINO: %s\n" % data[1:]
                        elif (data[0].lower() == 'n'):
                                android_queue.put_nowait(data[1:])
                                print "from PC to ANDROID: %s\n" % data[1:]
                        else:
                                print "incorrect header from PC"

        def write_pc(self, pc_queue):
                while 1:
                        if not pc_queue.empty():
                                data = pc_queue.get_nowait()
                                self.pc_thread.pc_write(data)
                                print "to PC: %s\n" %data

        def initialize_threads(self):

                android_read_thread = threading.Thread(target = self.read_an, args = (self.pc_queue,), name = "Thread - Android Read")
                android_write_thread = threading.Thread(target = self.write_an, args = (self.android_queue,), name = "Thread - Android Write")
                arduino_read_thread = threading.Thread(target = self.read_ar, args = (self.pc_queue,), name = "Thread - Arduino Read")
                arduino_write_thread = threading.Thread(target = self.write_ar, args = (self.arduino_queue,), name = "Thread - Arduino Write")
                pc_read_thread = threading.Thread(target = self.read_pc, args = (self.arduino_queue, self.android_queue), name = "Thread - PC Read")
                pc_write_thread = threading.Thread(target = self.write_pc, args=(self.pc_queue,), name = "Thread - PC Write")
                
                android_read_thread.daemon = True
                android_write_thread.daemon = True
                arduino_read_thread.daemon = True
                arduino_write_thread.daemon = True
                pc_read_thread.daemon = True
                pc_write_thread.daemon = True

                android_read_thread.start()
                android_write_thread.start()
                arduino_read_thread.start()
                arduino_write_thread.start()
                pc_read_thread.start()
                pc_write_thread.start()

                print("All threads initialized!")


        def disconnect_all(self):
                self.android_thread.disconnect()
                self.arduino_thread.disconnect()
                self.pc_thread.disconnect()
                print("All sockets closed!")


        def keep_main_alive(self):
                while 1: #ctrl-c to kill
                        time.sleep(0.5)


if __name__ == "__main__":
        try:
                runrun = myThread()
                runrun.initialize_threads()
                runrun.keep_main_alive()
                runrun.disconnect_all()
        except KeyboardInterrupt:
                runrun.disconnect_all()


