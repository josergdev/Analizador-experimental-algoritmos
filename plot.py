import matplotlib.pyplot as plt

with open('points.csv') as f:
    rows = f.readlines()
    rows = [row.strip().split(',') for row in rows]
    x = []
    y = []
    for row in rows:
        x.append(int(row[0]))
        y.append(int(row[1]))

plt.scatter(x, y)
plt.show()
