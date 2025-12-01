# Angular Mutation Generator
This is a project that automates the process of creating mutations 
for HTML files in Angular repositories.

### Project Structure
The project structure is as follows (only the relevant files are shown):
```txt
(root)
├── config.json // configuration file
├── pom.xml // maven dependencies
└── src/main/java/org.unina
    ├── 📁 core
    |   ├── 📁 matchers
    |   ├── 📁 rules
    |   ├── MutationRule // interface for the mutation rules
    |   ├── TagMatcher // interface for the tag matchers
    |   └── MutationEngine // file that applies the mutations
    ├── 📁 data
    |   ├── ComponentMetadata // gets all the files related to the current component
    |   ├── Config // class to map the config.json file
    |   └── ElementExtention // helper class to get more information on the target element
    └── 📁 util
        ├── FileBrowser
        └── RandomSelector // custom randomizer that can be seeded
```


