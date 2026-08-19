sed -i -e '/FunctionNaming:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/LongMethod:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/LongParameterList:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/TooManyFunctions:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/CyclomaticComplexMethod:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
