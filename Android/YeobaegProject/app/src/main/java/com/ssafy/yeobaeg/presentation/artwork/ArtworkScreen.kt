package com.ssafy.yeobaeg.presentation.artwork

import android.util.Log.w
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.*

// ArtExploreScreen.kt (상단 필터 아이콘에서 바텀시트 열고, 시트 컴포즈 추가)
@Composable
fun ArtExploreScreen(
    viewModel: ArtExploreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focus = LocalFocusManager.current
    var showFilter by remember { mutableStateOf(false) }

    BackHandler(enabled = state.mode == SearchMode.RESULT) {
        focus.clearFocus(true)
        viewModel.selectTabSearch()
    }
    BackHandler(enabled = state.mode == SearchMode.RECENT) {
        focus.clearFocus(true)
        viewModel.selectTabBrowse()
    }

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작품 탐색",
                onBackClick = {
                    when (state.mode) {
                        SearchMode.BROWSE -> {}
                        SearchMode.RECENT -> viewModel.selectTabBrowse()
                        SearchMode.RESULT -> viewModel.selectTabSearch()
                    }
                    focus.clearFocus(true)
                },
                showBackButton = state.mode != SearchMode.BROWSE
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "작품 둘러보기",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.weight(1f))
                    if (state.mode != SearchMode.RECENT) {
                        Icon(
                            painter = painterResource(R.drawable.ic_filter),
                            contentDescription = null,
                            modifier = Modifier.clickable { showFilter = true }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                SearchBar(
                    value = state.query,
                    onValueChange = viewModel::setQuery,
                    onSearch = { viewModel.search() },
                    onFocus = { viewModel.selectTabSearch() }
                )
            }
        },
        containerColor = Color.White
    ) { inner ->
        when (state.mode) {
            SearchMode.BROWSE -> BrowseContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                categories = state.categories,
                selected = state.selectedCategory,
                onCategory = viewModel::selectCategory,
                artworks = state.artworks
            )

            SearchMode.RECENT -> RecentSearchContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                recent = state.recent,
                onSearchClick = {
                    viewModel.setQuery(it)
                    viewModel.search()
                    focus.clearFocus()
                },
                onRemoveRecent = viewModel::removeRecent,
                onClearAll = viewModel::clearRecent
            )

            SearchMode.RESULT -> SearchResultContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                keyword = state.query,
                artists = state.resultArtist,
                artworks = state.resultArtworks
            )
        }
    }

    ArtworkFilterSheet(
        visible = showFilter,
        maxWidthCm = state.maxWidthCm,
        maxHeightCm = state.maxHeightCm,
        onApply = { w, h -> viewModel.setSizeFilter(w, h) },
        onDismiss = { showFilter = false }
    )
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onSearch: () -> Unit
) {
    YeobaegTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = "작품, 작가를 검색해 보세요",
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .onFocusChanged { state ->
                if (state.isFocused) onFocus()
            },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.clickable { onSearch() }
            )
        }
    )
}

@Composable
private fun BrowseContent(
    modifier: Modifier,
    categories: List<ArtworkCategory>,
    selected: ArtworkCategory,
    onCategory: (ArtworkCategory) -> Unit,
    artworks: List<String>
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(8.dp))
        CategoryChips(
            modifier = Modifier.padding(horizontal = 20.dp),
            categories = categories.map { it.label }, // 화면에는 라벨만 보여줌
            selected = selected.label,
            onClick = { label ->
                categories.firstOrNull { it.label == label }?.let(onCategory)
            }
        )
        Spacer(Modifier.height(8.dp))
        ArtworkGrid(
            items = artworks,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        )
    }
}


@Composable
private fun RecentSearchContent(
    modifier: Modifier,
    recent: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "최근 검색어",
                color = YeobaegDarkGray
            )
            Spacer(Modifier.weight(1f))
            if (recent.isNotEmpty()) {
                Text(
                    "전체 삭제",
                    fontSize = 13.sp,
                    color = YeobaegDarkGray,
                    modifier = Modifier.clickable { onClearAll() })
            }
        }
        Spacer(Modifier.height(12.dp))
        if (recent.isEmpty()) {
            Text("최근 검색어가 없습니다.", fontSize = 14.sp, color = YeobaegTextGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recent.forEach { keyword ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearchClick(keyword) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_recent),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            keyword,
                            color = YeobaegBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = null,
                            tint = YeobaegDarkRed,
                            modifier = Modifier.clickable { onRemoveRecent(keyword) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(YeobaegYellow, shape = MaterialTheme.shapes.large)
                .padding(16.dp)
        ) {
            Column {
                Text("검색 팁", color = YeobaegDarkBrown, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "• 작가 이름이나 작품 제목으로 검색해보세요\n• 장르나 스타일로도 검색이 가능합니다\n• 여러 키워드를 조합해서 검색해보세요",
                    fontSize = 12.sp,
                    color = YeobaegDarkGray
                )
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
fun SearchResultContent(
    modifier: Modifier,
    keyword: String,
    artists: List<ArtistResult>,
    artworks: List<String>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ArtistResultSection(artists = artists)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(text = "작품", modifier = Modifier.padding(8.dp), color = YeobaegTextGray)
        }
        items(artworks) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun ArtistResultSection(
    artists: List<ArtistResult>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text("작가", color = YeobaegTextGray)

        artists.forEach { artist ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = artist.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .height(48.dp)
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.large)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(artist.name, fontWeight = FontWeight.SemiBold)
                    Text(
                        artist.subtitle,
                        color = YeobaegTextGray,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Text("작품 ${artist.worksCount}개", fontSize = 12.sp, color = YeobaegTextGray)
                }
            }
        }
    }
}