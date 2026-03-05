command=$(emulator -list-avds | head -n 1)

emulator -avd "$command"