package com.github.ajsnarr98.linknotes.fake

import com.github.ajsnarr98.linknotes.data.TagTree
import com.github.ajsnarr98.linknotes.data.db.TagsDAO

class FirestoreTagsDAOFake : AbstractFirestoreDAOFake<TagTree, com.github.ajsnarr98.linknotes.data.db.firestore.TagTree>(), TagsDAO