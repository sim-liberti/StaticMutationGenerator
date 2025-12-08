# Angular Mutation Generator
This is a project that automates the process of creating mutations 
for HTML files in Angular repositories.

### Project Structure
The project structure is as follows (only the relevant files are shown):
```txt
(root)
├── generator-config.json
├── pom.xml
├── 📁 hook-injector
|   ├── Injector // file that injects the mutation hooks into the html files
|   └── App // main application file
├── 📁 mutation-generator
|   ├── 📁 core
|   |   ├── 📁 matchers
|   |   ├── 📁 rules
|   |   ├── MutationRule // interface for the mutation rules
|   |   ├── TagMatcher // interface for the tag matchers
|   |   └── MutationEngine // file that applies the mutations
|   ├── 📁 data
|   |   ├── Component // class to hold angular component data
|   |   ├── Config // class to map the config file
|   |   ├── MutationDatabase // class to hold all applied mutations
|   |   └── ElementExtention // static class to extend the jsoup element functionality
|   ├── 📁 util
|   |   ├── ComponentIndexer // maps every angular component once at the start of the program
|   |   └── RandomSelector // custom randomizer that can be seeded
|   └── App // main application file
```


