function fn() {
    karate.configure('connectTimeout', 20000);
    karate.configure('readTimeout', 20000);
    karate.configure('ssl', true);

    var eventDelay = 5000

    return {
        api: {
           baseUrl: 'http://localhost:8090/api/v1/actions-trail/',
           channelManagement: 'https://distribucion-digital-internal-qa.apps.ambientesbc.com/digital-distribution/api/v1/channel-management/field-parameter'
        },
        secrets: {
           rabbitMQ: 'nu0051001-d2b-local-rabbitmq-engagement-dbb'
        },
        aws: {
            endpoint: 'http://localhost:4566',
            region: 'us-east-1',
            s3: {
                bucketName: 'local-bucket'
            }
        },
        eventProperties: {
            eventDelay: eventDelay
        },
        parameters: {
            secureMailboxTransactionCode: '6161'
        }
    };
}