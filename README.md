# StaticMutationGenerator

Small Java project that generates static mutations for HTML templates.

## Generate documentation (Javadoc)

This project uses Maven. To generate the API documentation (Javadoc) locally run:

```bash
mvn javadoc:javadoc
```

The generated documentation will be placed under:

```
target/site/apidocs
```

Open `target/site/apidocs/index.html` in your browser to view the docs.

## Quick build

To compile and run tests:

```bash
mvn -q test
```

## Notes / Next steps

- If you want generated docs published to GitHub Pages or a site, consider configuring the Maven Site or GitHub Actions to run `mvn javadoc:javadoc` and publish `target/site/apidocs`.
- If you need docs for only certain packages, the Maven Javadoc plugin can be configured in `pom.xml`.

