import groovy.json.JsonSlurper

parsed_args = new JsonSlurper().parseText(args)

if ( repository.getRepositoryManager().get(parsed_args.name) ) {
  repository.getRepositoryManager().delete(parsed_args.name)
}
