function fn() {
    karate.configure('connectTimeout', 20000);
    karate.configure('readTimeout', 20000);
    karate.configure ('ssl', true);

    var baseUrl = karate.properties['baseUrl'] || 'https://#{business-service}#-internal-#{env}#.apps.#{domain-name}#.com'
    var namespace = karate.properties['namespace'] || '#{project-name}#'
    var rabbitMQSecret = karate.properties['rabbitMQSecret'] || '#{application-code}#-#{short-project-name}#-#{env}#-rabbitmq-engagement-#{channel-dbb}#'
    var channelManagement = karate.properties['channelManagement'] || 'https://distribucion-digital-internal-#{env}#.apps.ambientesbc.com/digital-distribution/api/v1/channel-management/parameter'
    var eventDelay = karate.properties['eventDelay'] || 5000
    var bucketName = karate.properties['bucket_name'] || '#{default-report-cloud-name}#'
    var secureMailboxTransactionCode = karate.properties['secureMailboxTransactionCode'] || '#{secure-mailbox-transaction-code}#'

    return {
        api: {
            baseUrl: baseUrl + '/' + namespace + '#{prefix}#' + '/' + '#{service}#',
            channelManagement: channelManagement
        },
        secrets: {
            rabbitMQ: rabbitMQSecret
        },
        aws: {
            region: 'us-east-1',
            s3: {
                bucketName: bucketName
            }
        },
        eventProperties: {
            eventDelay: eventDelay
        },
        parameters: {
            secureMailboxTransactionCode: secureMailboxTransactionCode
        }
    };
}