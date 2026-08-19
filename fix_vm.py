import sys

filepath = 'app/src/main/java/com/example/viewmodel/AuraViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

# Find where the duplicate starts
duplicate_start = content.find("idx.lifecycle.AndroidViewModel")

if duplicate_start != -1:
    # The duplicate is the original file starting from character 74!
    original_rest = content[duplicate_start:]
    
    # The first 74 characters were:
    # "package com.example.viewmodel\n\nimport android.app.Application\nimport andro"
    missing_prefix = "package com.example.viewmodel\n\nimport android.app.Application\nimport andro"
    
    original_file = missing_prefix + original_rest
    
    with open(filepath, 'w') as f:
        f.write(original_file)
    print("Restored original file")
else:
    print("Duplicate start not found")
