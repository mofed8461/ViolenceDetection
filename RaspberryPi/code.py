#! /usr/bin/env python

import speech_recognition as sr
import pyaudio
import wave
import RPi.GPIO as GPIO
import time
import os

ser = True
if ser:
    import serial
    try:
        ser = serial.Serial('/dev/ttyUSB0', 9600, timeout=1)#ttyAMA0
        ser.open()
        print("serial working")
    except:
        print("serial not working")

CHUNK = 4096
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100
DEVICE_INDEX = 2
RECORD_SECONDS = 5


def record(waveFilePath):
    global Button_x1_Pin_25_State
    global Button_x2_Pin_8_State
    global Button_x3_Pin_7_State
    global Button_x4_Pin_12_State
    global Button_x5_Pin_16_State
    global Button_x6_Pin_6_State
    global Button_x7_Pin_13_State

    p = pyaudio.PyAudio()
    stream =  p.open(format = FORMAT,
                    rate = RATE,
                    channels = CHANNELS,
                    input_device_index = DEVICE_INDEX,
                    input = True,
                    output = False,
                    frames_per_buffer = CHUNK)

    print("* recording")

    frames = []
    
    time.sleep(30)

    print("* done recording")

    stream.stop_stream()
    stream.close()
    p.terminate()

    wf = wave.open(waveFilePath, 'wb')
    wf.setnchannels(CHANNELS)
    wf.setsampwidth(p.get_sample_size(FORMAT))
    wf.setframerate(RATE)
    wf.writeframes(b''.join(frames))
    wf.close()
    return

def speechRec():

    text = ''
    r = sr.Recognizer()
    
    with sr.AudioFile('/home/pi/Desktop/data/rec.wav') as source:              # use "test.wav" as the audio source
        audio = r.record(source)                        # extract audio data from the file
    
    
    try:
        text = r.recognize_google(audio)
        print("recognized: " + text)   # recognize speech using Google Speech Recognition
    except sr.UnknownValueError:
        print("Could not understand audio")
    except sr.RequestError as e:
        print("Could not request results; {0}".format(e))
    except LookupError:                                 # speech is unintelligible
        print("Could not understand audio")

    result = False
    
    if 'punish' in text or 'hit' in text or 'hate' in text or 'one' in text or '1' in text:
        result = True
        ser.write(b"b\r\n");
        
        
    return result


while (True):
    os.system("arecord --format=S16_LE --duration=10 --rate=16000 -c1 /home/pi/Desktop/data/rec.wav");
    ##record('/home/pi/Desktop/data/rec.wav')
    print(speechRec())
