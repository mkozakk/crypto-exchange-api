import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let client = null;
const pending = [];

const ensureClient = () => {
  if (client) {
    return;
  }
  client = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    reconnectDelay: 3000,
    onConnect: () => {
      pending.forEach((sub) => sub());
      pending.length = 0;
    },
  });
  client.activate();
};

export const subscribe = (topic, callback) => {
  ensureClient();
  let subscription = null;

  const doSubscribe = () => {
    subscription = client.subscribe(topic, (message) => callback(JSON.parse(message.body)));
  };

  if (client.connected) {
    doSubscribe();
  } else {
    pending.push(doSubscribe);
  }

  return () => subscription && subscription.unsubscribe();
};
