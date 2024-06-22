# Email Service

This microservice is responsible for sending email messages and tracking their status. It receives message data asynchronously via the Message Broker (RabbitMQ) and stores it in a database (Elasticsearch). In case of successful sending it updates the message status, in case of error it saves the error information and tries to resend the message after certain intervals.

## Usage and Integration

- A REST API interface is available for processing email messages over HTTP.
- Use the `emailQueue` queue for use with other senders via the RabbitMQ message broker.

### Setting environment variables

Specify your data in the `.env` file in the project root directory:

```plaintext
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_email_password
```

### Installing the application in Docker

Run the command to start the installation and run in a Docker container:

```bash
docker-compose up --build
```

The application will start on port 8082. In development mode from the development environment, it starts on port 8081.

##  RabbitMQ Queue configuration in third-party services to send messages

For your service to work properly, you have to configure a RabbitMQ queue with the following parameters:
- Queue creation: `emailQueueue`.
- The queue must be declared with the parameter  `durable: true`. 

Example of integrating a third-party service to send notifications via messege broker
https://github.com/ValentynaKoriahina/nodejs-task4.git

