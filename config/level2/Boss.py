import sys
import math
import random

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.


# game loop
while True:
    oxygen = int(input())
    plastic_count = int(input())

    for _ in range(4):
        for field in input().split():
            pass

    # Write an answer using print
    # To debug: print("Debug messages...", file=sys.stderr)

    print(random.choice("NONE | UP | RIGHT | DOWN | LEFT".split(" | ")))
