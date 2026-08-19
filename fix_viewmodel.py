import sys

filepath = 'app/src/main/java/com/example/viewmodel/AuraViewModel.kt'
with open(filepath, 'r') as f:
    lines = f.readlines()

# We want to keep lines 0 to 217 (0-indexed)
# And lines 434 to end (0-indexed)

new_lines = lines[:218] + lines[434:]

with open(filepath, 'w') as f:
    f.writelines(new_lines)
print("Fixed AuraViewModel")
