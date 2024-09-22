import sys
import math
import random

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

# Write an action using print
# To debug: print("Debug messages...", file=sys.stderr, flush=True)

class Vector:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __add__(self, other):
        return Vector(self.x + other.x, self.y + other.y)

    def __mul__(self, val):
        return Vector(self.x * val, self.y * val)

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

    def __hash__(self):
        return hash((self.x, self.y))

    def __str__(self):
        return "Vector{" + "x=" + str(self.x) + ", y=" + str(self.y) + "}"

inverted_move = {
    "UP":"DOWN",
    "RIGHT":"LEFT",
    "DOWN":"UP",
    "LEFT":"RIGHT",
    "NONE":"NONE"
}

direction_to_move = {
    "y+":"UP",
    "x+":"RIGHT",
    "y-":"DOWN",
    "x-":"LEFT"
}

direction_to_vector = {
    "y+":Vector(0, 1),
    "x+":Vector(1, 0),
    "y-":Vector(0, -1),
    "x-":Vector(-1, 0)
}

move_to_vector = {
    "UP":Vector(0, 1),
    "RIGHT":Vector(1, 0),
    "DOWN":Vector(0, -1),
    "LEFT":Vector(-1, 0),
    "NONE":Vector(0, 0)
}


MAX_OXYGEN = 42


mode = "SCOUT"

current_pos = Vector(0, 0)

path = []
underwater_map = {}
coral_map = {}

# game loop
while True:
    # RETRIEVE INFOS
    oxygen = int(input())
    plastic_count = int(input())
    sonar = {}
    for i in range(4):
        sonnar = input()

        direction, infos = sonnar.split("=")
        material, raw_distance = infos.split("(")

        text_distance = ""
        i=0
        while raw_distance[i] != "m":
            text_distance += raw_distance[i]
            i+=1

        sonar[direction] = (material, int(text_distance))


    print("Oxygen:", oxygen, file=sys.stderr, flush=True)
    print("Plastic count:", plastic_count, file=sys.stderr, flush=True)
    print("Distances:", file=sys.stderr, flush=True)
    for direction in sonar:
        print(">", direction, sonar[direction], file=sys.stderr, flush=True)

    for direction in sonar:
        underwater_map[current_pos + direction_to_vector[direction] * (sonar[direction][1] + 1)] = 0 if sonar[direction][0] in ["CORAL", "SUBMARINE"] else 1

        if sonar[direction][0] == "CORAL":
            coral_map[current_pos + direction_to_vector[direction] * (sonar[direction][1]+ 1)] = -1

    print("Map:", file=sys.stderr, flush=True)
    for coordinates in underwater_map:
        print(coordinates, underwater_map[coordinates], file=sys.stderr, flush=True)

    # MODE TRANSITION
    if mode == "SCOUT":
        for direction in sonar:
            if sonar[direction][0] == "CORAL":
                mode = "TARGET"
        if oxygen <= len(path):
            mode = "SURFACE"
    elif mode == "TARGET":
        if plastic_count > 0:
            mode = "COLLECT"
        if oxygen <= len(path):
            mode = "SURFACE"
    elif mode == "COLLECT":
        if plastic_count == 0:
            mode = "SCOUT"
        if oxygen <= len(path):
            mode = "SURFACE"
    elif mode == "SURFACE":
        if oxygen == MAX_OXYGEN:
            mode = "SCOUT"

    # print(mode, file=sys.stderr, flush=True)


    # APPLY MODE
    move = "NONE"

    if mode == "SCOUT":
        furthest_direction = Vector(0, 0)

        for direction in sonar:
            furthest_direction = furthest_direction + direction_to_vector[direction] * sonar[direction][1] * (2**random.random())


        if abs(furthest_direction.y) > abs(furthest_direction.x):
            if furthest_direction.y > 0:
                move = "UP"
            else:
                move = "DOWN"
        else:
            if furthest_direction.x > 0:
                move = "RIGHT"
            else:
                move = "LEFT"

        path.append(move)

    elif mode == "TARGET":
        min_distance = math.inf
        best_direction = ""
        for direction in sonar:
            detected = sonar[direction]
            if detected[0] == "CORAL":
                if detected[1] < min_distance:
                    min_distance = detected[1]
                    best_direction = direction
        move = direction_to_move[best_direction]

    elif mode == "SURFACE":
        move = inverted_move[path.pop()]

    if mode != "SURFACE" and move != "NONE":
        path.append(move)

    current_pos += move_to_vector[move]

    print(mode, file=sys.stderr, flush=True)
    print("NONE")
