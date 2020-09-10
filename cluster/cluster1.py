import requests
import json
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer, TfidfTransformer
from sklearn.cluster import DBSCAN
import numpy as np
import heapq
from sklearn.cluster import KMeans
from collections import Counter


url = "https://covid-dashboard.aminer.cn/api/events/list?type=event&page=1&size=10000"
out = requests.get(url)
json_obj = out.json()
with open("events.json", "w") as file:
  file.write(json.dumps(json_obj))

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

Max_features = 50

vectorizer = TfidfVectorizer(
    max_df=0.5, max_features=Max_features, min_df=2, use_idf=True)
X = vectorizer.fit_transform(entity_label_join)
features = vectorizer.get_feature_names()
# print(type(X.toarray()[0]))


n_clusters = 10

final_cluster = {}
for i in range(n_clusters):
  final_cluster[str(i)] = {}
km = KMeans(n_clusters, init='k-means++', max_iter=300, n_init=1,
            verbose=False)
km.fit(X.toarray())
order_centroids = km.cluster_centers_.argsort()[:, ::-1]
terms = vectorizer.get_feature_names()
keywords_list = []
for i in range(n_clusters):
  keywords = []
  for ind in order_centroids[i, :5]:
    keywords.append(terms[ind])
  final_cluster[str(i)]['keywords'] = ' '.join(keywords)
  keywords_list.append(' '.join(keywords))
  print(f"Cluster {i}: {keywords_list[-1]}")
result = list(km.predict(X))
print('Cluster distribution:')
print(Counter(result))


for i in range(n_clusters):
  final_cluster[str(i)]['events'] = []
  for j, label in enumerate(result):
    if(label == i):
      final_cluster[str(i)]['events'].append(j)

with open("cluster.json", "w") as file:
  file.write(json.dumps(final_cluster))
