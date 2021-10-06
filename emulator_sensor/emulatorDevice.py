import argparse
import json
from datetime import datetime
import random
import hashlib
from random import randrange

def publish_messages(project_id, topic_name, data):
    """Publishes multiple messages to a Pub/Sub topic."""
    # [START pubsub_quickstart_publisher]
    # [START pubsub_publish]
    from google.cloud import pubsub_v1

    # TODO project_id = "Your Google Cloud Project ID"
    # TODO topic_name = "Your Pub/Sub topic name"

    publisher = pubsub_v1.PublisherClient()
    # The `topic_path` method creates a fully qualified identifier
    # in the form `projects/{project_id}/topics/{topic_name}`
    topic_path = publisher.topic_path(project_id, topic_name)


    # When you publish a message, the client returns a future.
    future = publisher.publish(topic_path, data=data)
    print('Published {} of message ID {}.'.format(data, future.result()))

    print('Published messages.')
    # [END pubsub_quickstart_publisher]
    # [END pubsub_publish]

def buid_msg():
    schema = {
        "sensorID":"",
        "uniqueID":"",
        "timecollected":None,
        "value":"",
        "deviceName":"",
        "deviceType":"",
    }

    print(randonNumber())
    deviceRandom = "device_"+str(randonNumber())
    schema["sensorID"]=deviceRandom
    schema["uniqueID"]=generaterandomHash1()
    # schema["timecollected"]=round(datetime.timestamp(datetime.now()))
    schema["value"]=round(random.uniform(1,2), 2)

   
    result = json.dumps(schema)
    
    return json.dumps(schema).encode("utf-8")
def randonNumber():
    
    return randrange(10)

def generaterandomHash(str2hash):
    result = hashlib.md5(str2hash.encode())
    return result.hexdigest()


def generaterandomHash1():
    result = ["VXnExcdkUY7uMWTlPcYY","XCXplgiNNtt28ExdWxmo"]
    return result[randrange(2)]
if __name__=='__main__':
  
    
    for n in range(1, 100):
        publish_messages('poc-datahack-ps-df-bq-01','pb-topic-raw',buid_msg())