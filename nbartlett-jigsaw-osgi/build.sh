# Clean modules dir
rm -rf modules
mkdir modules

# Copy non-Java resources
cd src
rsync -R $(find . -not -name '*.java') ../modules
cd -

# Compile modules
javac --module-source-path src -d modules $(find src -name '*.java')

# Build module JARs
rm -rf jars
mkdir jars
jar --create --file jars/nbartlett.jigsaw_osgi@1.0.jar --module-version=1.0 --main-class=nbartlett.osgi_jigsaw.Main -C modules/nbartlett.jigsaw_osgi .

## Thanks to Sander Mak's blog post, which helped with setting this up this build script.
