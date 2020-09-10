import requests
import json
url = "https://covid-dashboard.aminer.cn/api/events/list?type=event&page=1&size=5000"
out = requests.get(url)
json_obj = out.json()

# 下载完毕后进行处理
entity_label = []
entity_label_join = []
entity_seg = []
for item in json_obj["data"]:
  my_label = []
  label_join = ""
  for label in item["entities"]:
    label_join = label_join + " " + label["label"]
    my_label.append(label["label"])
  entity_label.append(my_label)
  entity_label_join.append(label_join)
  entity_seg.append(item["seg_text"])
# 得到分割的词与标签
# print(entity_label_join)

Max_features = 100
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer, TfidfTransformer
from sklearn.cluster import DBSCAN
import numpy as np
import heapq
from sklearn.cluster import KMeans
vectorizer = TfidfVectorizer(
    max_df=0.5, max_features=Max_features, min_df=2, use_idf=True)
X = vectorizer.fit_transform(entity_label_join)
features = vectorizer.get_feature_names()
# print(type(X.toarray()[0]))
db_labels = DBSCAN(eps=0.93, min_samples=6).fit(X.toarray())
labels = db_labels.labels_
# print(labels)
n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)
n_noise_ = list(labels).count(-1)
print(n_clusters_)
print(n_noise_)
cluster_label = {}
cluster_label[-1] = {}
feature_vector = X.toarray()
for i in range(n_clusters_):
  cluster_label[i] = {}  # 初始化

for i in range(len(labels)):
  it_label = labels[i]

  most_feature = heapq.nlargest(
      3, range(len(feature_vector[i])), feature_vector[i].take)
  for index in most_feature:
    if features[index] in cluster_label[it_label].keys():
      cluster_label[it_label][features[index]
                              ] = cluster_label[it_label][features[index]] + 1
    else:
      cluster_label[it_label][features[index]] = 1

print(cluster_label)

final_cluster = {}
final_cluster["-1"] = []
for i in range(n_clusters_):
  final_cluster[str(i)] = []

# print(len(labels))
for i in range(len(labels)):
  # print(i)
  belong = str(labels[i])
  final_cluster[belong].append(json_obj["data"][i])
# print(final_cluster)
with open("cluster.json", "w") as file:
  file.write(json.dumps(final_cluster))
