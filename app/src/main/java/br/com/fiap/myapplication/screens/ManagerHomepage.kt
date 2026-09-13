package br.com.fiap.myapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorHomeScreen() {

    // Aba selecionada da Bottom Navigation
    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    // Itens da Bottom Navigation
    val items = listOf(
        "Home",
        "Ideias",
        "Perfil"
    )

    // Ícones da Bottom Navigation
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.Lightbulb,
        Icons.Filled.Person
    )


    Scaffold(

        // ==========================================
        // BARRA SUPERIOR
        // ==========================================
        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "Olá, Gestor!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Bem-vindo ao painel da Águia Branca",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },

                // Foto/perfil no canto direito
                actions = {

                    Surface(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape),

                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",

                            modifier = Modifier
                                .padding(8.dp),

                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },


        // ==========================================
        // BOTTOM NAVIGATION
        // ==========================================
        bottomBar = {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {

                // Percorre os itens da lista
                items.forEachIndexed { index, item ->

                    NavigationBarItem(

                        icon = {

                            Icon(
                                imageVector = icons[index],
                                contentDescription = item
                            )
                        },

                        label = {
                            Text(item)
                        },

                        selected = selectedItem == index,

                        onClick = {
                            selectedItem = index
                        }
                    )
                }
            }
        }

    ) { paddingValues ->


        // ==========================================
        // CONTEÚDO PRINCIPAL
        // ==========================================
        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            // Espaçamento inicial
            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }


            // ==========================================
            // CARD - GESTÃO DE PROJETOS
            // ==========================================
            item {

                DashboardCard(

                    title = "Gestão de Projetos",

                    subtitle = "Acompanhe e atualize as iniciativas",

                    icon = Icons.Default.Checklist,

                    onClick = {
                        // Futuramente:
                        // navegar para a tela de projetos
                    }
                )
            }


            // ==========================================
            // CARD - IDEIAS PENDENTES
            // ==========================================
            item {

                DashboardCard(

                    title = "Ideias Pendentes",

                    subtitle = "Avalie e priorize sugestões do time",

                    icon = Icons.Default.TipsAndUpdates,

                    onClick = {
                        // Futuramente:
                        // navegar para aprovação de ideias
                    }
                )
            }


            // ==========================================
            // CARD - ESTRATÉGIAS
            // ==========================================
            item {

                DashboardCard(

                    title = "Diretrizes Estratégicas",

                    subtitle = "Consulte as metas vigentes da liderança",

                    icon = Icons.Default.TrendingUp,

                    onClick = {
                        // Futuramente:
                        // navegar para estratégias
                    }
                )
            }


            // Espaço no final da lista
            item {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}


// ==================================================
// COMPONENTE DO CARD
// ==================================================

@Composable
fun DashboardCard(

    title: String,

    subtitle: String,

    icon: ImageVector,

    onClick: () -> Unit

) {

    ElevatedCard(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        colors = CardDefaults.elevatedCardColors(

            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.elevatedCardElevation(

            defaultElevation = 2.dp
        )
    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // ==========================================
            // ÍCONE
            // ==========================================

            Surface(

                shape = CircleShape,

                color =
                    MaterialTheme.colorScheme.secondaryContainer,

                modifier = Modifier.size(48.dp)
            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    modifier = Modifier.padding(12.dp),

                    tint =
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }


            // Espaçamento
            Spacer(
                modifier = Modifier.width(16.dp)
            )


            // ==========================================
            // TEXTOS
            // ==========================================

            Column(

                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    style =
                        MaterialTheme.typography.titleMedium,

                    color =
                        MaterialTheme.colorScheme.onSurface,

                    fontWeight =
                        FontWeight.SemiBold
                )


                Text(

                    text = subtitle,

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            // ==========================================
            // SETA
            // ==========================================

            Icon(

                imageVector =
                    Icons.Rounded.ChevronRight,

                contentDescription =
                    "Ir para $title",

                tint =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ==================================================
// PREVIEW
// ==================================================

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Preview - Gestor Home"
)
@Composable
fun GestorHomeScreenPreview() {

    MaterialTheme {

        GestorHomeScreen()
    }
}