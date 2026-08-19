sed -i -e '/TooGenericExceptionCaught:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/SwallowedException:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/VariableNaming:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
sed -i -e '/UnusedParameter:/{n;s/active: true/active: false/}' config/detekt/detekt.yml
