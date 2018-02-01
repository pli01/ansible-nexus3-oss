import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

def existingRepository = repository.getRepositoryManager().get(parsed_args.name)

if (existingRepository != null) {
//    existingRepository.stop()

    newConfig = existingRepository.configuration.copy()
    // We only update values we are allowed to change (cf. greyed out options in gui)
    newConfig.attributes['docker']['forceBasicAuth'] = parsed_args.force_basic_auth
    newConfig.attributes['docker']['v1Enabled'] = parsed_args.v1_enabled
    newConfig.attributes['storage']['writePolicy'] = parsed_args.write_policy.toUpperCase()
    newConfig.attributes['storage']['strictContentTypeValidation'] = Boolean.valueOf(parsed_args.strict_content_validation)
    if (parsed_args.http_port) {
        newConfig.attributes['docker'].httpPort = parsed_args.http_port
    } else {
        newConfig.attributes['docker'].httpPort = ""
    }
    newConfig.attributes['storage']['blobStoreName'] = existingRepository.configuration.attributes['storage']['blobStoreName']
    existingRepository.stop()
    existingRepository.update(newConfig)
    existingRepository.start()
} else {
  configuration = new Configuration(
          repositoryName: parsed_args.name,
          recipeName: 'docker-hosted',
          online: true,
          attributes: [
                  docker: [
                          forceBasicAuth: parsed_args.force_basic_auth,
                          httpPort: parsed_args.http_port,
                          v1Enabled : parsed_args.v1_enabled
                  ],
                  storage: [
                          writePolicy: parsed_args.write_policy.toUpperCase(),
                          blobStoreName: parsed_args.blob_store,
                          strictContentTypeValidation: Boolean.valueOf(parsed_args.strict_content_validation)
                  ]
          ]
  )
    repository.getRepositoryManager().create(configuration)
}
