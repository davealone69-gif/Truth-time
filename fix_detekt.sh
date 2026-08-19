sed -i -e '/MagicNumber:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/MaxLineLength:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/WildcardImport:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/UseCheckOrError:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/UnusedPrivateProperty:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
